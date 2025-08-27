# SelectableListCard

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/ui/components/cards/SelectableListCard.kt`

Card with selectable state and animated checkmark badge.

Usage:
```kotlin
SelectableListCard(
    isSelected = selected,
    onClick = { selected = !selected }
) {
    // row content
}
```
Notes:
- Applies primary border and tinted container color when selected.
- Animates size and check icon with scale + fade.
