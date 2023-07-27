package com.swirlfist.desastre.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.ReminderTimeUnit
import com.swirlfist.desastre.ui.theme.DesastreTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun DayPicker() {
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .width(320.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val childModifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

        DayPickerElementWrapper {
            TodayTomorrow(modifier = childModifier)
        }
        DayPickerElementWrapper {
            AmountOfTimeSelector(modifier = childModifier)
        }
        DayPickerElementWrapper {
            DateSelector(
                modifier = childModifier,
                onShowDatePicker = { showDatePickerDialog = true },
            )
        }
        if (showDatePickerDialog) {
            DatePicker(
                onDismiss = { showDatePickerDialog = false }
            )
        }
    }
}

@Composable
fun DayPickerElementWrapper(
    wrapped: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        wrapped()
    }
}

@Composable
fun TodayTomorrow(
    modifier: Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = modifier,
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(
                text = stringResource(R.string.today),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Button(onClick = { /*TODO*/ }) {
            Text(
                text = stringResource(R.string.tomorrow),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun AmountOfTimeSelector(
    modifier: Modifier,
) {
    var amount by rememberSaveable { mutableStateOf<Int?>(1) }
    var chosenReminderTimeUnit by rememberSaveable { mutableStateOf(ReminderTimeUnit.Day) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypeAmount(
                modifier = Modifier
                    .width(72.dp),
                amount,
                min = 1,
                max = 999999,
                onTypedAmountChanged = { amount = it }
            )
            ChooseOption(
                chosenOption = chosenReminderTimeUnit,
                options = listOf(
                    Pair(ReminderTimeUnit.Day, pluralStringResource(R.plurals.day, amount?: 1)),
                    Pair(ReminderTimeUnit.Week, pluralStringResource(R.plurals.week, amount?:1)),
                    Pair(ReminderTimeUnit.Month, pluralStringResource(R.plurals.month, amount?: 1)),
                    Pair(ReminderTimeUnit.Year, pluralStringResource(R.plurals.year, amount?: 1)),
                ),
                onChosenOptionChanged = {
                    chosenReminderTimeUnit = it
                },
                modifier = Modifier
                    .width(144.dp),
            )
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            val timeFromNow = pluralStringResource(
                id = when (chosenReminderTimeUnit) {
                    ReminderTimeUnit.Day -> R.plurals.x_days
                    ReminderTimeUnit.Week -> R.plurals.x_weeks
                    ReminderTimeUnit.Month -> R.plurals.x_months
                    ReminderTimeUnit.Year -> R.plurals.x_years
                    else -> R.plurals.x_days
                },
                count = amount?: 1,
            ).format(amount?: 1)
            Text(
                text = stringResource(
                    R.string.time_from_now,
                    timeFromNow,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun DateSelector(
    modifier: Modifier,
    onShowDatePicker: () -> Unit,
    ) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = onShowDatePicker,
        ) {
            Text(
                text = stringResource(R.string.pick_a_date),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun TypeAmount(
    modifier: Modifier = Modifier,
    amount: Int?,
    min: Int = 1,
    max: Int = Int.MAX_VALUE,
    onTypedAmountChanged: (Int?) -> Unit,
) {
    TextField(
        value = amount?.toString()?: "",
        onValueChange = { onTypedAmountChanged(processAmount(it, min, max)) },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
    )
}

private fun processAmount(
    amountStr: String,
    min: Int,
    max: Int,
): Int? {
    val processedAmountStr = amountStr.replace(Regex.fromLiteral("[^0-9]+"), "")
    if (processedAmountStr.isEmpty()) {
        return null
    }
    val amount = amountStr.toInt()
    return if (amount < min) min else if (amount > max) max else amount
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseOption(
    modifier: Modifier = Modifier,
    chosenOption: ReminderTimeUnit = ReminderTimeUnit.Minute,
    options: List<Pair<ReminderTimeUnit, String>>,
    onChosenOptionChanged: (ReminderTimeUnit) -> Unit,
    ) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.wrapContentSize(),
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .width(IntrinsicSize.Min),
            readOnly = true,
            value = options.firstOrNull{ it.first == chosenOption }?.second ?: "",
            singleLine = true,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        expanded = false
                        onChosenOptionChanged(option.first)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .background(
                            color =
                            if (option.first == chosenOption) MaterialTheme.colorScheme.secondary
                            else Color.Transparent
                        ),
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    /* TODO */
                    Log.d("gus", "${datePickerState.selectedDateMillis}")
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            dateValidator = { dateMillis -> run{
                    val today = LocalDate.now()
                    val chosenDate = LocalDate.from(Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()))
                    chosenDate.isEqual(today) || chosenDate.isAfter(today)
                }
            }
        )
    }
}

@Preview(widthDp = 320, heightDp = 640, showBackground = true)
@Composable
fun DayPickerPreview() {
    DesastreTheme {
        DayPicker()
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerPreview() {
    DesastreTheme {
        DatePicker(
            onDismiss = {},
        )
    }
}