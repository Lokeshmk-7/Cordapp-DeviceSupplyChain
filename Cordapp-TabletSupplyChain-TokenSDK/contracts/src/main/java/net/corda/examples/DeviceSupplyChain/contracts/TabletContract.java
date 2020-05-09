package net.corda.examples.DeviceSupplyChain.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class TabletContract extends EvolvableTokenContract implements Contract {
    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {

        List<ContractState> inputs = tx.getInputStates();
        List<ContractState> outputs = tx.getOutputStates();

//            if (inputs.size()!= 0 ) throw new IllegalArgumentException("Inputs have to be 0");
            requireThat(req -> {
                //shape
                req.using("inputs must be 0", inputs.size() == 0);
                req.using("outputs must be 1", outputs.size() == 1);
                req.using("output is of type TokenState", outputs.get(0) instanceof TabletToken);

                //content
                TabletToken tokenstate = (TabletToken) outputs.get(0);
                LocalDate d= LocalDate.now();
                int currentYear = d.getYear();
                req.using("Qunatity must be greater than 0", tokenstate.getQuantity() >0);
                req.using("Date of manufacture is greater than equal to today's date", tokenstate.getDom() < currentYear);
                req.using("Date of expiry is less than equal to today's date", tokenstate.getDoe() > currentYear );
                req.using("amount must be greater than 0",tokenstate.getAmount() > 0);


                return null;
            });

    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {

    }

}
