package cn.deercare.enums;


public enum PayType {
    UNLIMITED(1), // 随意
    MULTIPLE(2); // 倍数

    private final Integer value;

    PayType(Integer value){
        this.value = value;
    }
    public Integer getValue(){
        return value;
    }
}
