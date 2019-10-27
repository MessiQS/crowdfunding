package cn.deercare.enums;

import com.baomidou.mybatisplus.extension.api.R;

public enum ProjectHotelState {
    B("b"), // 众筹开始
    F("f"), // 众筹失败
    S("s"), // 众筹成功
    P("p"), // 工厂生产（推送)
    L("l"), // 酒店铺设（推送）
    O("o"), // 开始运营（推送）
    T("t"), // 自行填补
    R("r"); // 众筹失败退款

    private final String value;

    ProjectHotelState(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
