package com.example.jetsplit.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    // Calculate tip amount only (splitBy is not required here)
    return if (totalBill > 1) {
        (totalBill * tipPercentage) / 100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int, // Changed to Int for consistency
    tipPercentage: Int
): Double {
    // Calculate the total bill including tip
    val bill = calculateTotalTip(
        totalBill = totalBill,
        tipPercentage = tipPercentage
    ) + totalBill

    // Split the total bill among the number of people
    return (bill / splitBy)
}