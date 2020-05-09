package net.corda.examples.DeviceSupplyChain.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveNonFungibleTokensHandler;
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken;
import kotlin.Unit;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

public class TransferTabletFlow {

    public TransferTabletFlow() {
    }

    @InitiatingFlow
    @StartableByRPC
    public static class TransferTabletToken extends FlowLogic<String> {

        private final String name;
        private final String batchno;
        private final int dom;
        private final int doe;
        private final int quantity;
        private final int amount;
        private final Party holder;

        public TransferTabletToken(String name, String batchno, int dom, int doe, int quantity, int amount, Party holder) {
            this.name = name;
            this.batchno = batchno;
            this.dom = dom;
            this.doe = doe;
            this.quantity = quantity;
            this.amount = amount;
            this.holder = holder;
        }

        @Suspendable
        @Override
        public String call() throws FlowException {

            StateAndRef<TabletToken> frameStateandRef = getServiceHub().getVaultService()
                    .queryBy(TabletToken.class).getStates().stream()
                    .filter(sf -> sf.getState().getData().getName().equals(this.name)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException("StockState symbol=\"" + this.name + "\" not found from vault"));

            TabletToken tabletTokenState = frameStateandRef.getState().getData();

            TokenPointer tabletTokenPointer = tabletTokenState.toPointer(tabletTokenState.getClass());

            PartyAndToken partyAndToken = new PartyAndToken(holder, tabletTokenPointer);

            SignedTransaction stx = (SignedTransaction) subFlow(new MoveNonFungibleTokens(partyAndToken));


            return "\nTransfer ownership of a tablet name: "+ this.name + " to "
                    + this.holder.getName().getOrganisation() + "\nTransaction IDs: "
                    + stx.getId();
        }
    }


    @InitiatedBy(TransferTabletToken.class)
    public static class TransferTabletTokenResponder extends FlowLogic<Unit>{

        private FlowSession counterSession;

        public TransferTabletTokenResponder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {

            return subFlow(new MoveNonFungibleTokensHandler(counterSession));

        }
    }

}
