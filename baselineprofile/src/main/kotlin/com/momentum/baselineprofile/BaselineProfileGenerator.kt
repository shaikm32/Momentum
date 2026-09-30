package com.momentum.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ADR-45 / TASK-012 baseline profile generator.
 *
 * Two `collect` blocks, per the official two-pass guidance (task.md 8.7.5):
 *  - [journey]  warm-up + the full tab walk. Never benchmarked, only recorded, so
 *               the app reaches steady state before the measured pass.
 *  - [startup]  cold startup only, so the startup/AOT-critical classes get
 *               top-priority compilation.
 *
 * `collect` reinstalls the app before each block, so onboarding is present at the
 * start of BOTH blocks. It is always dismissed with the `Skip onboarding` content
 * description, which is present on all four onboarding pages and finishes
 * immediately with `seedExamples = false` - that makes the journey deterministic
 * and independent of the page count. This is not a workaround for ISSUE-029
 * (onboarding re-shows on every cold start); that issue is deliberately untouched.
 *
 * The same reinstall also resets `POST_NOTIFICATIONS`, so
 * [grantNotificationPermission] runs in both blocks too - see its KDoc for why it
 * is mandatory rather than cosmetic.
 *
 * With no seeded example data the lists are empty, so rendering is stable. Every
 * interaction is a `wait`-guarded find - never a fixed sleep - and the journey
 * stays narrow: no create flow, detail, calendar, settings, themes or scroll.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    // Pass 1 - warm-up + full journey.
    @Test
    fun journey() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        profileBlock = { journeyToHomeThenAcrossAllTabs() },
    )

    // Pass 2 - cold startup only.
    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        profileBlock = {
            // MainActivity is the MAIN/LAUNCHER activity, so this is a true cold
            // start of the reinstalled app.
            grantNotificationPermission()
            startActivityAndWait()
            dismissOnboarding()
        },
    )

    // An extension on the `profileBlock` receiver: `startActivityAndWait()` is a
    // member of MacrobenchmarkScope in benchmark-macro 1.5.0, so it is only in
    // scope inside the block.
    private fun MacrobenchmarkScope.journeyToHomeThenAcrossAllTabs() {
        grantNotificationPermission()
        startActivityAndWait()
        dismissOnboarding()
        // Home -> Habits -> Tasks -> Insights -> Home. Each label is the Text
        // inside a clickable LiquidGlassTab column (ADR-22): there is no separate
        // "Activities" tab - Tasks is the activities list surface.
        for (tab in TABS) {
            device.wait(Until.hasObject(By.text(tab)), UI_TIMEOUT_MS)
            device.findObject(By.text(tab))?.click()
            device.waitForIdle()
        }
    }

    /** `cd_skip_onboarding` is on all four onboarding pages. */
    private fun dismissOnboarding() {
        device.findObject(By.desc(SKIP_ONBOARDING))?.click()
        device.wait(Until.hasObject(By.text(HOME_TAB)), UI_TIMEOUT_MS)
    }

    /**
     * `MainActivity.requestNotificationPermissionIfNeeded()` fires
     * `POST_NOTIFICATIONS` on every cold start, so the system permission dialog
     * (`GrantPermissionsActivity`) is raised on top of the app repeatedly during
     * the collection - the archived `logcat-...-journey.txt` shows it being
     * displayed 9 times in one `journey` block.
     *
     * This has to be handled or the whole run fails. The dialog holds the
     * foreground, so the app behind it is never the visible window, and - the
     * fatal one - `AndroidBenchmarkRunner` cannot complete its `IsolationActivity`
     * check while the dialog is up. That surfaces as the synthetic
     * `Test mechanism` failure (`Could not launch intent
     * ...androidx.benchmark.IsolationActivity within 45000 milliseconds`) while
     * both real tests are reported as PASSED.
     *
     * The permission is granted over `adb shell` rather than clicked in the
     * dialog. Clicking needs a selector on another app's window, and neither
     * `By.res("com.android.permissioncontroller:id/permission_deny_button")` nor
     * `By.text("Don't allow")` matched even though the dialog was demonstrably
     * displayed (both "Node not found" in the archived logcat). Granting is also
     * idempotent and repeatable across the several relaunches a single block
     * performs, which one successful click would not have been.
     *
     * Only the generator changes. The app's own permission behaviour is untouched,
     * no manifest entry or permission is added to `:app`, and the app under test
     * is the same APK.
     */
    private fun grantNotificationPermission() {
        // Result discarded on purpose: the return type differs across the
        // uiautomator versions this could resolve to (ParcelFileDescriptor in
        // 2.3.0, String in the 2.4.0 that `benchmark-macro-junit4:1.5.0` pulls
        // in), and the command's output is not needed.
        device.executeShellCommand("pm grant $PACKAGE_NAME $POST_NOTIFICATIONS")
    }

    private val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private companion object {
        const val PACKAGE_NAME = "com.momentum.app"
        const val UI_TIMEOUT_MS = 5_000L
        const val SKIP_ONBOARDING = "Skip onboarding"
        const val HOME_TAB = "Home"
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        val TABS = listOf("Habits", "Tasks", "Insights", "Home")
    }
}
