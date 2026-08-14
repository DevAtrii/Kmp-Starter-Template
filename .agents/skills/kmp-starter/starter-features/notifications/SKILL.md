---
name: kmp-starter-feature-notifications
description: The KMP Starter Template notifications system (alarmee) — StarterNotificationsManager for local/scheduled notifications, push setup, and Koin module registration.
author: DevAtrii
license: MIT

---

# Notifications (alarmee)

Local, scheduled, and push notifications via the `alarmee` library. Three modules under `features/notifications/`.

## Where things live

| Piece | Module | Path |
| --- | --- | --- |
| `AppNotifications` (channel id/name) | notifications core | `features/notifications/core/.../AlarmeeNotifications.kt` |
| `createAlarmeePlatformConfiguration()` (expect) | notifications core | same file |
| `StarterNotificationsManager` (interface) | notifications local | `features/notifications/local/.../StarterNotificationsManager.kt` |
| `StarterNotificationsManagerImpl` | notifications local | `.../StarterNotificationsManagerImpl.kt` |
| DI | core/local/push | `NotificationsCoreModule.kt`, `NotificationsLocalModule.kt`, `NotificationsPushModule.kt` |

## Local & scheduled notifications

`StarterNotificationsManager` is the API:

```kotlin
interface StarterNotificationsManager {
    companion object Companion {
        fun randomUuid(): String
    }

    val scheduledUUIDs: Flow<Set<String>>

    fun schedule(alarmee: Alarmee)   // schedule for a specific time of day
    fun immediate(alarmee: Alarmee)  // send immediately
    fun cancel(uuid: String)         // cancel a scheduled alarm
}
```

Usage (typically injected via Koin):

```kotlin
class ReminderViewModel(
    private val notificationsManager: StarterNotificationsManager,
) : MviViewModel<ReminderState, ReminderActions, ReminderEvents>() {

    fun scheduleReminder() {
        notificationsManager.schedule(alarmee = Alarmee(...))
    }
}
```

`StarterNotificationsManagerImpl` persists scheduled UUIDs to DataStore and delegates to `LocalNotificationService`.

## Push

`notificationsPushModule` initializes the mobile `alarmee` service and exposes its `push` stream. Android/iOS platform config comes from `createAlarmeePlatformConfiguration()`.

## Koin

Register all three modules in `InitKoin` (see koin skill):

```kotlin
notificationsCoreModule
notificationsLocalModule
notificationsPushModule
```

`notificationsLocalModule` binds `StarterNotificationsManagerImpl` → `StarterNotificationsManager`.

## Rules

- Reuse the alarmee manager; don't roll your own notification scheduling.
- Register the notification Koin modules in `InitKoin`.

## Reference

- Docs: `https://starter.atherio.dev/` (features / notifications)
- Source: `features/notifications/*`
