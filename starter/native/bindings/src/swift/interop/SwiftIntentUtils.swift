//
//  SwiftIntentUtils.swift
//  iosApp
//
//  Created by Athar Gul on 21/08/2025.
//

import UIKit
import Foundation

/// Platform-specific link utilities for iOS that provides URL opening and email functionality
/// This class is designed to be called from Kotlin Multiplatform code
@objc public class SwiftIntentUtils: NSObject {
    
    // MARK: - Public Static Methods
    
    /// Opens a URL in the default browser or appropriate application
    /// - Parameters:
    ///   - str: The URL string to open
    ///   - completion: Optional completion handler that returns success status and error message
    @objc public static func openUrl(str: String) {
        openUrl(str: str, completion: nil)
    }
    
    /// Opens a URL in the default browser or appropriate application with completion handler
    /// - Parameters:
    ///   - str: The URL string to open
    ///   - completion: Completion handler that returns success status and error message
    @objc public static func openUrl(str: String, completion: ((Bool, String?) -> Void)?) {
        // Input validation
        guard !str.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            let errorMessage = "URL string is empty or contains only whitespace"
            completion?(false, errorMessage)
            return
        }
        
        // Clean and prepare URL string
        let cleanUrlString = str.trimmingCharacters(in: .whitespacesAndNewlines)
        
        // Try to create URL, add https:// if no scheme is present
        var urlToOpen: URL?
        
        if let url = URL(string: cleanUrlString) {
            urlToOpen = url
        } else if !cleanUrlString.hasPrefix("http://") && !cleanUrlString.hasPrefix("https://") {
            // Try adding https:// prefix for domain-only URLs
            urlToOpen = URL(string: "https://\(cleanUrlString)")
        }
        
        guard let finalUrl = urlToOpen else {
            let errorMessage = "Invalid URL format: \(cleanUrlString)"
            completion?(false, errorMessage)
            return
        }
        
        // Check if the URL can be opened
        guard UIApplication.shared.canOpenURL(finalUrl) else {
            let errorMessage = "Cannot open URL: \(finalUrl.absoluteString)"
            completion?(false, errorMessage)
            return
        }
        
        // Open URL with completion handling
        UIApplication.shared.open(finalUrl, options: [:]) { success in
            DispatchQueue.main.async {
                if success {
                    completion?(true, nil)
                } else {
                    completion?(false, "Failed to open URL: \(finalUrl.absoluteString)")
                }
            }
        }
    }
    
    /// Sends an email using the default mail application
    /// - Parameters:
    ///   - email: Recipient email address
    ///   - subject: Email subject
    ///   - body: Email body content
    @objc public static func sendEmail(email: String, subject: String, body: String) {
        sendEmail(email: email, subject: subject, body: body, completion: nil)
    }
    
    /// Sends an email using the default mail application with completion handler
    /// - Parameters:
    ///   - email: Recipient email address
    ///   - subject: Email subject
    ///   - body: Email body content
    ///   - completion: Completion handler that returns success status and error message
    @objc public static func sendEmail(email: String, subject: String, body: String, completion: ((Bool, String?) -> Void)?) {
        // Input validation
        guard !email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            let errorMessage = "Email address cannot be empty"
            completion?(false, errorMessage)
            return
        }
        
        // Basic email validation
        guard isValidEmail(email) else {
            let errorMessage = "Invalid email address format: \(email)"
            completion?(false, errorMessage)
            return
        }
        
        // Encode subject and body for URL
        let subjectEncoded = subject.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        let bodyEncoded = body.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        
        // Create mailto URL
        let mailtoString = "mailto:\(email)?subject=\(subjectEncoded)&body=\(bodyEncoded)"
        
        guard let mailtoUrl = URL(string: mailtoString) else {
            let errorMessage = "Failed to create mailto URL"
            completion?(false, errorMessage)
            return
        }
        
        // Check if mail can be sent
        guard UIApplication.shared.canOpenURL(mailtoUrl) else {
            let errorMessage = "No email application available or email not configured"
            completion?(false, errorMessage)
            return
        }
        
        // Open mail application
        UIApplication.shared.open(mailtoUrl, options: [:]) { success in
            DispatchQueue.main.async {
                if success {
                    completion?(true, nil)
                } else {
                    completion?(false, "Failed to open email application")
                }
            }
        }
    }
    
    // MARK: - Private Helper Methods
    
    /// Validates email address format using regex
    /// - Parameter email: Email address to validate
    /// - Returns: True if email format is valid, false otherwise
    private static func isValidEmail(_ email: String) -> Bool {
        let emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        let emailPredicate = NSPredicate(format: "SELF MATCHES %@", emailRegex)
        return emailPredicate.evaluate(with: email)
    }
    
    // MARK: - Additional Utility Methods
    
    /// Checks if a specific URL scheme can be handled by the device
    /// - Parameter urlScheme: The URL scheme to check (e.g., "mailto", "tel", "sms")
    /// - Returns: True if the scheme can be handled, false otherwise
    @objc public static func canHandleUrlScheme(_ urlScheme: String) -> Bool {
        guard let url = URL(string: "\(urlScheme)://") else { return false }
        return UIApplication.shared.canOpenURL(url)
    }
    
    /// Opens phone dialer with the provided phone number
    /// - Parameters:
    ///   - phoneNumber: Phone number to dial
    ///   - completion: Optional completion handler
    @objc public static func makePhoneCall(phoneNumber: String, completion: ((Bool, String?) -> Void)? = nil) {
        let cleanNumber = phoneNumber.components(separatedBy: CharacterSet.decimalDigits.inverted).joined()
        guard !cleanNumber.isEmpty else {
            completion?(false, "Invalid phone number")
            return
        }
        
        let telUrl = "tel://\(cleanNumber)"
        guard let url = URL(string: telUrl), UIApplication.shared.canOpenURL(url) else {
            completion?(false, "Cannot make phone calls on this device")
            return
        }
        
        UIApplication.shared.open(url, options: [:]) { success in
            DispatchQueue.main.async {
                completion?(success, success ? nil : "Failed to open phone dialer")
            }
        }
    }
    
    /// Opens SMS application with the provided phone number
    /// - Parameters:
    ///   - phoneNumber: Phone number for SMS
    ///   - message: Pre-filled message (optional)
    ///   - completion: Optional completion handler
    @objc public static func sendSMS(phoneNumber: String, message: String = "", completion: ((Bool, String?) -> Void)? = nil) {
        let cleanNumber = phoneNumber.components(separatedBy: CharacterSet.decimalDigits.inverted).joined()
        guard !cleanNumber.isEmpty else {
            completion?(false, "Invalid phone number")
            return
        }
        
        let messageEncoded = message.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        let smsUrl = message.isEmpty ? "sms://\(cleanNumber)" : "sms://\(cleanNumber)?body=\(messageEncoded)"
        
        guard let url = URL(string: smsUrl), UIApplication.shared.canOpenURL(url) else {
            completion?(false, "Cannot send SMS on this device")
            return
        }
        
        UIApplication.shared.open(url, options: [:]) { success in
            DispatchQueue.main.async {
                completion?(success, success ? nil : "Failed to open SMS application")
            }
        }
    }
}
