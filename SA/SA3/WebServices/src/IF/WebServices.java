package IF;

public interface WebServices {
    public String sendEmail(String _url,String _payload);
    public String sendEmailBatch(String[] _url,String _payload);
    public String validateEmailAddress(String _url);
}
