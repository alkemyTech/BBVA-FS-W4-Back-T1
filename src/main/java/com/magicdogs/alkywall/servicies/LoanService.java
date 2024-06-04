package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.dto.LoanDTO;
import com.magicdogs.alkywall.dto.LoanSimulateDTO;
import org.springframework.stereotype.Service;


@Service
public class LoanService {

    public Object simulate(LoanSimulateDTO loanSimulateDTO) {
        var amount = loanSimulateDTO.getAmount();
        var term = loanSimulateDTO.getTerm();
        var interest = Constants.getLoanInterestPercent(); //Mensual

        var monthSubscription = amount/term + (amount*interest); //Monto solicitado dividido la cantidad de meses mas el interes mensual
        var totalAmount = monthSubscription * term; //Monto total

        var loan = new LoanDTO();
        loan.setInterest(interest);
        loan.setMonthSubscription(truncateToTwoDecimals(monthSubscription));
        loan.setTotalAmount(truncateToTwoDecimals(totalAmount));

        return loan;
    }

    private static double truncateToTwoDecimals(double value) {
        return Math.floor(value * 100) / 100;
    }
}
