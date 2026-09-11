package com.momentum.app.notification

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P2#3: only broadcasts that invalidate armed RTC_WAKEUP wall-clock alarms
 * may trigger the central resync. Midnight rollover (DATE_CHANGED) moves no
 * armed instant, so it must NOT reschedule.
 *
 * Full timezone behavior (alarm fires at the correct local wall-clock time,
 * exactly once) additionally requires manual device verification: change the
 * device timezone with an armed reminder and observe one correctly-timed
 * notification.
 */
class SystemEventReceiverTest {

    @Test
    fun `boot triggers resync`() {
        assertTrue(shouldResyncOnSystemBroadcast(Intent.ACTION_BOOT_COMPLETED))
    }

    @Test
    fun `timezone change triggers resync`() {
        assertTrue(shouldResyncOnSystemBroadcast(Intent.ACTION_TIMEZONE_CHANGED))
    }

    @Test
    fun `manual clock change triggers resync`() {
        assertTrue(shouldResyncOnSystemBroadcast(Intent.ACTION_TIME_CHANGED))
    }

    @Test
    fun `unrelated broadcasts do not resync`() {
        assertFalse(shouldResyncOnSystemBroadcast(null))
        assertFalse(shouldResyncOnSystemBroadcast(Intent.ACTION_DATE_CHANGED))
        assertFalse(shouldResyncOnSystemBroadcast("android.intent.action.PACKAGE_ADDED"))
        assertFalse(shouldResyncOnSystemBroadcast(""))
    }
}
