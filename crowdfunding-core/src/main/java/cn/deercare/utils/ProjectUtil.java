package cn.deercare.utils;

import cn.deercare.enums.PayType;
import cn.deercare.enums.ProjectHotelState;
import cn.deercare.enums.ProjectType;
import cn.deercare.model.ProjectHotel;
import org.springframework.beans.factory.annotation.Autowired;

import static cn.deercare.enums.ProjectHotelState.*;

public class ProjectUtil {

    public static ProjectType getProjectType(Integer type){
        switch (type){
            case 1:
                return ProjectType.HOTEL;
        }
        return null;
    }

    public static ProjectHotelState getProjectHotelNextState(ProjectHotel projectHotel){
        switch (getProjectHotelState(projectHotel.getState())){
            case B:
                // 判断众筹是否成功
                if(projectHotel.getAccountAmount().compareTo(projectHotel.getAmount()) != -1){
                    return S;
                }
                return F;
            case F:
                return R;
            case S:
                return P;
            case P:
                return L;
            case L:
                return O;
        }
        return null;
    }

    public static ProjectHotelState getProjectHotelState(String state){
        switch (state){
            case "b":
                return B;
            case "f":
                return F;
            case "s":
                return S;
            case "p":
                return P;
            case "l":
                return L;
            case "o":
                return O;
            case "t":
                return T;
            case "r":
                return R;
        }
        return null;
    }

    public static PayType getProjectPayType(Long paymentId){
        switch (Integer.parseInt(paymentId.toString())){
            case 1:
                return PayType.UNLIMITED;
            case 2:
                return PayType.MULTIPLE;
        }
        return null;
    }
}
