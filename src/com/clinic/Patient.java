package com.clinic;

class Patient {
    private final String nic;
    private final String name;
    private final String email;
    private final String phone;

    public Patient(String nic, String name, String email, String phone) {
        this.nic = nic;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getNic() { return nic; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}


