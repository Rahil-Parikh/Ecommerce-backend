package com.hub.ecommerce.models.admin.response;

import java.util.List;

public class RazorpayOrder{
    public String id;
    public String entity;
    public int amount;
    public int amount_paid;
    public int amount_due;
    public String currency;
    public Long receipt;
    public Object offer_id;
    public String status;
    public int attempts;
    public List<Object> notes;
    public int created_at;
}