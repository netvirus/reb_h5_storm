package l2r.gameserver.model.payment;

/**
 * @author netvirus
 */

public class Payment {
    private int _id;
    private String _recipient;
    private int _amount;
    private String _dateComplete;
    private boolean _delivered;

    public Payment(int id, String recipient, int amount, String dateComplete, boolean delivered)
    {
        this._id = id;
        this._recipient = recipient;
        this._amount = amount;
        this._dateComplete = dateComplete;
        this._delivered = delivered;
    }

    public int getId()
    {
        return _id;
    }

    public String getRecipient()
    {
        return _recipient;
    }

    public void setRecipient(String recipient)
    {
        _recipient = recipient;
    }

    public int getAmount()
    {
        return _amount;
    }

    public void setAmount(int amount)
    {
        _amount = amount;
    }

    public String getDateComplete() { return _dateComplete; }

    public void setDateComplete(String dateComplete) { _dateComplete = dateComplete; }

    public boolean getDeliveryStatus() { return _delivered; }

    public void setDeliveryStatus(boolean status)
    {
        _delivered = status;
    }
}
