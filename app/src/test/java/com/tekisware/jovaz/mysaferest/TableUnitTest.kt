package com.tekisware.jovaz.mysaferest

import com.tekisware.jovaz.mysaferest.model.DataManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TableUnitTest {

    @Before
    fun setUp() {
    }

    @Test
    fun indexForTableWithinRange_isCorrect() {
        Assert.assertNotNull(DataManager.getTable(0))
    }

    @Test
    fun indexForTableUnderRange_returnsNull() {
        Assert.assertNull(DataManager.getTable(-1))
    }

    @Test
    fun indexForTableOverRange_returnsNull() {
        Assert.assertNull(DataManager.getTable(8))
    }

    @Test(expected = IllegalArgumentException::class)
    fun customersCountUnderRangeOnTableReserved_throwsArgumentException() {
        val table = DataManager.getTable(0)
        table?.setReservedFor(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun customersCountUnderRangeOnTableBusy_throwsArgumentException() {
        val table = DataManager.getTable(0)
        table?.setBusyWith(0)
    }
}