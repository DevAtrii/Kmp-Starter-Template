package com.kmpstarter.ui_utils.focus_manager

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager

fun FocusManager.moveDown() = moveFocus(FocusDirection.Down)

fun FocusManager.moveUp() = moveFocus(FocusDirection.Up)

fun FocusManager.moveNext() = moveFocus(FocusDirection.Next)

fun FocusManager.movePrevious() = moveFocus(FocusDirection.Previous)

fun FocusManager.moveEnter() = moveFocus(FocusDirection.Enter)

fun FocusManager.moveExit() = moveFocus(FocusDirection.Exit)

fun FocusManager.moveLeft() = moveFocus(FocusDirection.Left)

fun FocusManager.moveRight() = moveFocus(FocusDirection.Right)