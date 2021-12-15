package com.example.demo;

import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;

public class Agent {
    public MultipartFile image;
    public String name;
    public String promoCode;
    public String telephoneNumber;
    public String region;
    public String email;
    public byte[] pic;
    public boolean hasPic;
    public String status;
    public String identifier;

    public Agent(String status, String name, String hasPic, String promoCode, String telephoneNumber, String region, String email,String ID){
        this.name=name;
        this.status = status;
        this.promoCode=promoCode;
        this.telephoneNumber=telephoneNumber;
        this.region=region;
        this.email = email;
        this.identifier = ID;
    }

    public Agent() {

    }

    ByteArrayInputStream getPic() {
        return new ByteArrayInputStream(pic);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String ID) {
        this.identifier = ID;
    }
}
