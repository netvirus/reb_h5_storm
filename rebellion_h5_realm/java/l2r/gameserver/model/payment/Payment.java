package l2r.gameserver.model.payment;

/**
 * @author netvirus
 */

public class Payment {
    private int _id;
    private String _recipient;
    private int _amount;
    private int _payDate;
    private int _status;

    public Payment(int id, String recipient, int amount, int payDate, int status)
    {
        this._id = id;
        this._recipient = recipient;
        this._amount = amount;
        this._payDate = payDate;
        this._status = status;
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

    public int getPayDate()
    {
        return _payDate;
    }

    public void setPayDate(int payDate)
    {
        _payDate = payDate;
    }

    public int getStatus()
    {
        return _status;
    }

    public void setStatus(int status)
    {
        _status = status;
    }
}
