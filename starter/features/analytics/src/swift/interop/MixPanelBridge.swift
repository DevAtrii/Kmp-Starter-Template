//
// Created by Athar Gul on 18/02/2026.
//

import Foundation
import Mixpanel

@objc public class MixPanelBridge: NSObject {

    private let mixpanel: MixpanelInstance
    private var isEnabled: Bool = true

    @objc public init(
        token: String,
        trackAutomaticEvents: Bool,
        flushInterval: Int,
        enabled: Bool = true
    ) {

        Mixpanel.initialize(
            token: token,
            trackAutomaticEvents: trackAutomaticEvents,
            flushInterval: Double(flushInterval) // SDK expects Double
        )

        self.mixpanel = Mixpanel.mainInstance()
        self.isEnabled = enabled
        super.init()
    }

    // MARK: - Track (no properties)

    @objc public func track(event: String) {
        guard isEnabled else { return }
        mixpanel.track(event: event)
    }

    // MARK: - Track (single key/value)

    @objc public func track(
        event: String,
        key: String?,
        value: Any?
    ) {
        guard isEnabled else { return }

        var properties: Properties?

        if let key = key,
           let value = value as? MixpanelType {
            properties = [key: value]
        }

        mixpanel.track(event: event, properties: properties)
        flush()
    }

    // MARK: - Track (dictionary)

    @objc public func track(
        event: String,
        properties: [String: Any]?
    ) {
        guard isEnabled else { return }

        var converted: Properties?

        if let properties = properties {
            var temp: Properties = [:]
            for (key, value) in properties {
                if let casted = value as? MixpanelType {
                    temp[key] = casted
                }
            }
            converted = temp
        }

        mixpanel.track(event: event, properties: converted)
        flush()
    }

    // MARK: - User

    @objc public func setUserId(_ userId: String) {
        guard isEnabled else { return }
        mixpanel.identify(distinctId: userId)
    }

    // MARK: - Opt In / Out

    @objc public func optIn() {
        mixpanel.optInTracking()
    }

    @objc public func optOut() {
        mixpanel.optOutTracking()
    }

    @objc public func toggleOptInOut() {
        if mixpanel.hasOptedOutTracking() {
            optIn()
        } else {
            optOut()
        }
    }

    @objc public func hasOptedIn() -> Bool {
        return !mixpanel.hasOptedOutTracking()
    }

    // MARK: - Maintenance

    @objc public func flush() {
        mixpanel.flush()
    }

    @objc public func reset() {
        mixpanel.reset()
    }

    @objc public func setEnabled(_ enabled: Bool) {
        self.isEnabled = enabled
    }
}
