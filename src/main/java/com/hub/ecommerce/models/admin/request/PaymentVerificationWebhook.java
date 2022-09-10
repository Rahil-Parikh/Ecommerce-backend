package com.hub.ecommerce.models.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
class Card implements Serializable{
    public String id;
    public String entity;
    public String name;
    public String last4;
    public String network;
    public String type;
    public Object issuer;
    public boolean international;
    public boolean emi;
    public String sub_type;

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", entity='" + entity + '\'' +
                ", name='" + name + '\'' +
                ", last4='" + last4 + '\'' +
                ", network='" + network + '\'' +
                ", type='" + type + '\'' +
                ", issuer=" + issuer +
                ", international=" + international +
                ", emi=" + emi +
                ", sub_type='" + sub_type + '\'' +
                '}';
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Notes implements Serializable{
    public String address;

    @Override
    public String toString() {
        return "Notes{" +
                "address='" + address + '\'' +
                '}';
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class AcquirerData implements Serializable{
    public String auth_code;

    @Override
    public String toString() {
        return "AcquirerData{" +
                "auth_code='" + auth_code + '\'' +
                '}';
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Entity implements Serializable{
    public String id;
    public String entity;
    public int amount;
    public String currency;
    public String status;
    public String order_id;
    public Object invoice_id;
    public boolean international;
    public String method;
    public int amount_refunded;
    public Object refund_status;
    public boolean captured;
    public String description;
    public String card_id;
    public Card card;
    public Object bank;
    public Object wallet;
    public Object vpa;
    public String email;
    public String contact;
    public Notes notes;
    public int fee;
    public int tax;
    public Object error_code;
    public Object error_description;
    public Object error_source;
    public Object error_step;
    public Object error_reason;
    public AcquirerData acquirer_data;
    public int created_at;

    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
                ", entity='" + entity + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", order_id='" + order_id + '\'' +
                ", invoice_id=" + invoice_id +
                ", international=" + international +
                ", method='" + method + '\'' +
                ", amount_refunded=" + amount_refunded +
                ", refund_status=" + refund_status +
                ", captured=" + captured +
                ", description='" + description + '\'' +
                ", card_id='" + card_id + '\'' +
                ", card=" + card +
                ", bank=" + bank +
                ", wallet=" + wallet +
                ", vpa=" + vpa +
                ", email='" + email + '\'' +
                ", contact='" + contact + '\'' +
                ", notes=" + notes +
                ", fee=" + fee +
                ", tax=" + tax +
                ", error_code=" + error_code +
                ", error_description=" + error_description +
                ", error_source=" + error_source +
                ", error_step=" + error_step +
                ", error_reason=" + error_reason +
                ", acquirer_data=" + acquirer_data +
                ", created_at=" + created_at +
                '}';
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Payment implements Serializable{
    public Entity entity;

    @Override
    public String toString() {
        return "Payment{" +
                "entity=" + entity +
                '}';
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Payload implements Serializable{
    public Payment payment;

    @Override
    public String toString() {
        return "Payload{" +
                "payment=" + payment +
                '}';
    }

    public Payment getPayment() {
        return payment;
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationWebhook implements Serializable {
    public String entity;
    public String account_id;
    public String event;
    public List<String> contains;
    public Payload payload;
    public int created_at;

    public String getOrder_Id(){
        return payload.getPayment().getEntity().getOrder_id();
    }

    @Override
    public String toString() {
        return "PaymentVerificationWebhook{" +
                "entity='" + entity + '\'' +
                ", account_id='" + account_id + '\'' +
                ", event='" + event + '\'' +
                ", contains=" + contains +
                ", payload=" + payload +
                ", created_at=" + created_at +
                '}';
    }
}

