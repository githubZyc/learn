package strategy.prize;

/**
 * 功能描述:
 * 礼物明细
 * @Class SendPrizeRequest
 * @Author ZYC
 * @Date 2021/3/26 14:21
 * @Version 1.0
 **/
public class SendPrizeRequest {
    private String prizeId;
    private int size;
    private String userId;
    private PrizeTypeEnum prizeType;


    public SendPrizeRequest() {
    }

    public SendPrizeRequest(String prizeId, int size, String userId, PrizeTypeEnum prizeType) {
        this.prizeId = prizeId;
        this.size = size;
        this.userId = userId;
        this.prizeType = prizeType;
    }

    enum PrizeTypeEnum{
        POINT,
        CASH,
        VIRTUAL_CURRENCY;
    }

    public String getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(String prizeId) {
        this.prizeId = prizeId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PrizeTypeEnum getPrizeType() {
        return prizeType;
    }

    public void setPrizeType(PrizeTypeEnum prizeType) {
        this.prizeType = prizeType;
    }
}
