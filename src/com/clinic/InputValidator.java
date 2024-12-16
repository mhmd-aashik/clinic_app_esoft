package com.clinic;


class InputValidator {
    public static boolean isValidNic(String nic) {
        if (nic == null) {
            return false; // NIC cannot be null
        }

        // Regex patterns for old and new NIC formats
        String oldNicPattern = "^[0-9]{9}[VX]$";  // Old format: 9 digits + V or X
        String newNicPattern = "^[0-9]{12}$";     // New format: 12 digits

        // Validate against both patterns
        return nic.matches(oldNicPattern) || nic.matches(newNicPattern);
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false; // Name cannot be null or empty
        }

        // Regex pattern for valid names
        String namePattern = "^[A-Za-z]+([ '-][A-Za-z]+)*$";

        // Check length and pattern
        return name.length() >= 4 && name.length() <= 50 && name.matches(namePattern);
    }


    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Email cannot be null or empty
        }

        // Regex for lowercase email validation
        String emailPattern = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$";

        return email.matches(emailPattern);
    }
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false; // Phone number cannot be null or empty
        }

        // Regex patterns for Sri Lankan phone numbers
        String mobilePattern = "^07[0-9]{8}$";           // Mobile numbers (e.g., 0712345678)
        String landlinePattern = "^0[1-9][0-9]{8}$";     // Landline numbers (e.g., 0112345678)
        String internationalPattern = "^\\+94[0-9]{9}$"; // International format (e.g., +94712345678)

        // Validate against all patterns
        return phone.matches(mobilePattern) ||
                phone.matches(landlinePattern) ||
                phone.matches(internationalPattern);
    }
}