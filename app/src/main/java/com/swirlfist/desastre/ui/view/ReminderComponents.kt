package com.swirlfist.desastre.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.ReminderTimeUnit
import com.swirlfist.desastre.ui.TodoSelectableDates
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.util.isForToday
import com.swirlfist.desastre.util.isForTomorrow
import com.swirlfist.desastre.util.isInThePast
import com.swirlfist.desastre.util.toShortFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

private const val MIN_REMINDER_MINUTES = 60L

@Composable
fun Reminders(
    reminders: List<Reminder>,
    backgroundColor: Color,
    onAddReminder: () -> Unit,
    onEditReminder: (Reminder) -> Unit,
) {
    val listState = rememberLazyListState()

    ScrollableContentWithGradients(
        canScrollBackward = listState.canScrollBackward,
        canScrollForward = listState.canScrollForward,
        gradientColor = backgroundColor,
        gradientWidth = 32.dp,
    ) {
        LazyRow(
            state = listState,
            userScrollEnabled = true,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(backgroundColor)
                .wrapContentHeight(),
        ) {
            item {
                AddOrEditReminderButton(
                    text = "+",
                    strikeText = false,
                    onClick = onAddReminder,
                )
            }
            items(reminders) { reminder ->
                ReminderItem(reminder, onEditReminder)
            }
        }
    }
}

@Composable
fun ScrollableContentWithGradients(
    modifier: Modifier = Modifier,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    gradientColor: Color,
    gradientWidth: Dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = addGradients(
            gradientColor,
            gradientWidth,
            content
        ),
    ) { measurableList, constraints ->
        val contentPlaceable = measurableList.first().measure(constraints)
        val gradientConstraints = constraints.copy(
            maxHeight = contentPlaceable.height,
            minHeight = contentPlaceable.height,
        )
        val startGradientPlaceable = if (canScrollBackward) {
            measurableList[1].measure(gradientConstraints)
        } else {
            null
        }
        val endGradientPlaceable = if (canScrollForward) {
            measurableList[2].measure(gradientConstraints)
        } else {
            null
        }

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
            startGradientPlaceable?.placeRelative(0, 0)
            endGradientPlaceable?.placeRelative(contentPlaceable.width - endGradientPlaceable.width, 0)
        }
    }
}

fun addGradients(
    gradientColor: Color,
    gradientWidth: Dp,
    content: @Composable () -> Unit,
) = @Composable {
    content()
    HorizontalScrollableGradient(
        color = gradientColor,
        width = gradientWidth,
        isStart = true,
    )
    HorizontalScrollableGradient(
        color = gradientColor,
        width = gradientWidth,
        isStart = false,
    )
}

@Composable
fun HorizontalScrollableGradient(
    modifier: Modifier = Modifier,
    color: Color,
    width: Dp = 32.dp,
    isStart: Boolean = false,
) {
    val gradientColorList = with(listOf(Color.Transparent, color)) {
        if (isStart) this.asReversed() else this
    }

    Spacer(
        modifier = modifier
            .width(width)
            .background(Brush.horizontalGradient(gradientColorList))
    )
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onReminderClick: (Reminder) -> Unit,
) {

    val dateText = if (reminder.isForToday()) {
        stringResource(R.string.today)
    } else if (reminder.isForTomorrow()) {
        stringResource(id = R.string.tomorrow)
    } else {
        reminder.time.toLocalDate().toShortFormat()
    }

    val timeText = reminder.time.toLocalTime().toShortFormat()

    AddOrEditReminderButton(
        text = "$dateText @ $timeText",
        strikeText = reminder.isInThePast(),
        onClick = { onReminderClick(reminder) },
    )
}

@Composable
fun AddOrEditReminderButton(
    text: String,
    strikeText: Boolean = false,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(
            text = text,
            style = (
                    if (strikeText) {
                        MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        MaterialTheme.typography.bodySmall
                    }
                    )
        )
    }
}

@Composable
fun DaySelector(
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null,
) {
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

        if (initialDate != null && !initialDate.isBefore(LocalDate.now())) {
            PickerElementWrapper {
                QuickSelection(
                    modifier = childModifier,
                    initialDate,
                    { date -> date.toShortFormat() },
                    onDateSelected,
                )
            }
        }

        PickerElementWrapper {
            TodayTomorrow(
                modifier = childModifier,
                onDateSelected,
            )
        }
        PickerElementWrapper {
            AmountOfTimeSelector(
                modifier = childModifier,
                forToday = false,
                onAmountOfTimeSelected = { amount, reminderTimeUnit ->
                    onDateSelected(getDateFromToday(amount, reminderTimeUnit))
                },
            )
        }
        PickerElementWrapper {
            PickerSelector(
                modifier = childModifier,
                text = stringResource(R.string.pick_a_date),
                onShowPicker = { showDatePickerDialog = true },
            )
        }
        if (showDatePickerDialog) {
            DatePicker(
                onDismiss = { showDatePickerDialog = false },
                onDateSelected,
                initialDate,
            )
        }
    }
}

@Composable
fun TimeSelector(
    forToday: Boolean,
    onTimeSelected: (localDateTime: LocalDateTime) -> Unit,
    initialTime: LocalTime? = null,
) {
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
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
        val hours = arrayOf(9, 12, 15, 18, 21).filter { hour -> !forToday || LocalTime.now().hour < hour }

        if (!forToday && initialTime != null) {
            PickerElementWrapper {
                QuickSelection(
                    modifier = childModifier,
                    initialTime,
                    { time -> time.toShortFormat() },
                ) { time -> onTimeSelected(time.atDate(LocalDate.now())) }
            }
        }

        if (forToday) {
            PickerElementWrapper {
                AmountOfTimeSelector(
                    modifier = childModifier,
                    forToday = true,
                    onAmountOfTimeSelected = { amount, reminderTimeUnit -> onTimeSelected(getTimeFromNow(amount, reminderTimeUnit)) },
                )
            }
        }

        if (hours.isNotEmpty()) {
            PickerElementWrapper {
                PresetTimes(
                    modifier = childModifier,
                    hours,
                    onTimeSelected,
                )
            }
        }

        PickerElementWrapper {
            PickerSelector(
                modifier = childModifier,
                text = stringResource(R.string.pick_a_time),
                onShowPicker = { showTimePickerDialog = true },
            )
        }
        if (showTimePickerDialog) {
            TimePicker(
                forToday,
                onTimeSelected,
                onDismiss = { showTimePickerDialog = false },
                initialTime,
            )
        }
    }
}

@Composable
fun PickerElementWrapper(
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
fun <T> QuickSelection(
    modifier: Modifier,
    value: T,
    formatter: (T) -> String,
    onSelected: (T) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = modifier,
    ) {
        ReminderButton(
            text = formatter(value),
            onClick = { onSelected(value) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayTomorrow(
    modifier: Modifier,
    onDateSelected: (LocalDate) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = modifier,
    ) {
        ReminderButton(stringResource(R.string.today)) { onDateSelected(LocalDate.now()) }
        ReminderButton(stringResource(R.string.tomorrow)) { onDateSelected(LocalDate.now().plusDays(1)) }
    }
}

@Composable
fun AmountOfTimeSelector(
    modifier: Modifier,
    forToday: Boolean,
    onAmountOfTimeSelected: (Int, ReminderTimeUnit) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf<Int?>(1) }
    var chosenReminderTimeUnit by rememberSaveable {
        mutableStateOf(if (forToday) ReminderTimeUnit.Hour else ReminderTimeUnit.Day)
    }
    val options = if (forToday) {
        listOf(
            Pair(ReminderTimeUnit.Minute, pluralStringResource(R.plurals.minute, amount?: 1)),
            Pair(ReminderTimeUnit.Hour, pluralStringResource(R.plurals.hour, amount?:1)),
        )
    } else {
        listOf(
            Pair(ReminderTimeUnit.Day, pluralStringResource(R.plurals.day, amount?: 1)),
            Pair(ReminderTimeUnit.Week, pluralStringResource(R.plurals.week, amount?:1)),
            Pair(ReminderTimeUnit.Month, pluralStringResource(R.plurals.month, amount?: 1)),
            Pair(ReminderTimeUnit.Year, pluralStringResource(R.plurals.year, amount?: 1)),
        )
    }

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
                options = options,
                onChosenOptionChanged = {
                    chosenReminderTimeUnit = it
                },
                modifier = Modifier
                    .width(144.dp),
            )
        }
        val timeFromNow = pluralStringResource(
            id = when (chosenReminderTimeUnit) {
                ReminderTimeUnit.Minute -> R.plurals.x_minutes
                ReminderTimeUnit.Hour -> R.plurals.x_hours
                ReminderTimeUnit.Day -> R.plurals.x_days
                ReminderTimeUnit.Week -> R.plurals.x_weeks
                ReminderTimeUnit.Month -> R.plurals.x_months
                ReminderTimeUnit.Year -> R.plurals.x_years
            },
            count = amount?: 1,
        ).format(amount?: 1)
        ReminderButton(
            text = stringResource(R.string.time_from_now, timeFromNow),
            enabled = isValidSelectedAmount(forToday, amount ?: 1, chosenReminderTimeUnit),
        ) { onAmountOfTimeSelected(amount ?: 1, chosenReminderTimeUnit) }
    }
}

@Composable
fun PickerSelector(
    modifier: Modifier,
    text: String,
    onShowPicker: () -> Unit,
    ) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        ReminderButton(
            text,
            onClick = onShowPicker,
        )
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

@Composable
fun ReminderButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun processAmount(
    amountStr: String,
    min: Int,
    max: Int,
): Int? {
    val processedAmountStr = amountStr.replace(Regex("[^0-9]+"), "")
    if (processedAmountStr.isEmpty()) {
        return null
    }
    val amount = processedAmountStr.toInt()
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
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.atStartOfDay()?.atOffset(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
        selectableDates = TodoSelectableDates(LocalDate.now()))
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (datePickerState.selectedDateMillis != null) {
                        onDateSelected(LocalDate.from(
                            Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                                .atOffset(ZoneOffset.UTC)))
                    }
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
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PresetTimes(
    modifier: Modifier,
    hours: List<Int>,
    onTimeSelected: (localDateTime: LocalDateTime) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),

        modifier = modifier,
    ) {
        hours.forEach { hour -> PresetTimeButton(onTimeSelected, hour) }
    }
}

@Composable
fun PresetTimeButton(
    onTimeSelected: (localDateTime: LocalDateTime) -> Unit,
    hour: Int,
    minute: Int = 0,
) {
    ReminderButton(text = "${"%02d".format(hour)}:${"%02d".format(minute)}") {
        onTimeSelected(LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute)))
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    forToday: Boolean,
    onTimeSelected: (localDateTime: LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
    initialTime: LocalTime? = null,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 0,
        initialMinute = initialTime?.minute ?: 0,
        is24Hour = true
    )
    val configuration = LocalConfiguration.current

    TimePickerDialog(
        onCancel = onDismiss,
        onConfirm = {
            onTimeSelected(LocalDateTime.of(LocalDate.now(), LocalTime.of(timePickerState.hour, timePickerState.minute)))
        },
        validSelectedTime = isValidSelectedTime(forToday, timePickerState.hour, timePickerState.minute)
    ) {
        if (configuration.screenHeightDp > 600) {
            TimePicker(
                state = timePickerState,
            )
        } else {
            TimeInput(
                state = timePickerState,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = stringResource(R.string.select_time),
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    validSelectedTime: Boolean,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 8.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel,
                    ) { Text(stringResource(android.R.string.cancel)) }
                    TextButton(
                        onClick = onConfirm,
                        enabled = validSelectedTime,
                    ) { Text(stringResource(android.R.string.ok)) }
                }
            }
        }
    }
}

private fun isValidSelectedAmount(
    forToday: Boolean,
    amount: Int,
    chosenReminderTimeUnit: ReminderTimeUnit,
): Boolean {
    if (!forToday || chosenReminderTimeUnit != ReminderTimeUnit.Minute) {
        return true
    }

    return amount >= MIN_REMINDER_MINUTES
}

private fun isValidSelectedTime(
    forToday: Boolean,
    hour: Int,
    minute: Int
) : Boolean {
    if (!forToday) {
        return true
    }

    val selected = LocalTime.of(hour, minute)
    val min = LocalTime.now().plusMinutes(MIN_REMINDER_MINUTES - 1)

    return selected.isAfter(min)
}

private fun getDateFromToday(
    amount: Int,
    reminderTimeUnit: ReminderTimeUnit
): LocalDate {
    return when(reminderTimeUnit) {
        ReminderTimeUnit.Day -> LocalDate.now().plusDays(amount.toLong())
        ReminderTimeUnit.Week -> LocalDate.now().plusWeeks(amount.toLong())
        ReminderTimeUnit.Month -> LocalDate.now().plusMonths(amount.toLong())
        ReminderTimeUnit.Year -> LocalDate.now().plusYears(amount.toLong())
        else -> LocalDate.now()
    }
}

private fun getTimeFromNow(
    amount: Int,
    reminderTimeUnit: ReminderTimeUnit
): LocalDateTime {
    return when(reminderTimeUnit) {
        ReminderTimeUnit.Minute -> LocalDateTime.now().plusMinutes(amount.toLong())
        ReminderTimeUnit.Hour -> LocalDateTime.now().plusHours(amount.toLong())
        else -> LocalDateTime.now()
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun RemindersPreview() {
    DesastreTheme {
        Reminders(
            reminders = PreviewUtil.mockReminders(4),
            backgroundColor = MaterialTheme.colorScheme.surface,
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun RemindersEmptyPreview() {
    DesastreTheme {
        Reminders(
            reminders = listOf(),
            backgroundColor = MaterialTheme.colorScheme.surface,
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview
@Composable
fun ReminderItemPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemTodayPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockTodayReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemTomorrowPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockTomorrowReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemPastPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockYesterdayReminder()) {}
    }
}

@Preview(widthDp = 320, showBackground = true)
@Composable
fun DaySelectorPreview() {
    DesastreTheme {
        DaySelector({ selectedDate -> Log.d("Preview", "$selectedDate") })
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerPreview() {
    DesastreTheme {
        DatePicker(
            onDismiss = {},
            onDateSelected = { selectedDate -> Log.d("Preview", "$selectedDate") }
        )
    }
}

@Preview(widthDp = 320, showBackground = true)
@Composable
fun TimeSelectorForTodayPreview() {
    DesastreTheme {
        TimeSelector(
            forToday = true,
            onTimeSelected = { selectedTime -> Log.d("Preview", "$selectedTime") }
        )
    }
}

@Preview(widthDp = 320, showBackground = true)
@Composable
fun TimeSelectorNotForTodayPreview() {
    DesastreTheme {
        TimeSelector(
            forToday = false,
            onTimeSelected = { selectedTime -> Log.d("Preview", "$selectedTime") }
        )
    }
}

@Preview(name="time-picker", showBackground = true, heightDp = 640)
@Preview(name="time-input", showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun TimePickerForTodayPreview() {
    DesastreTheme {
        TimePicker(
            forToday = true,
            onTimeSelected = { selectedTime -> Log.d("Preview", "$selectedTime") },
            onDismiss = {},
        )
    }
}

