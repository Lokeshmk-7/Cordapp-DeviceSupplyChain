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
import net.corda.examples.DeviceSupplyChain.states.MicroscopeToken;

public class TransferDeviceFlow {

    public TransferDeviceFlow() {
    }

    @InitiatingFlow
    @StartableByRPC
    public static class TransferDeviceToken extends FlowLogic<String> {

        private final String name;
        private final String batchno;
        private final int dom;
        private final int amount;
        private final Party holder;

        public TransferDeviceToken(String name, String batchno, int dom, int amount, Party holder) {
            this.name = name;
            this.batchno = batchno;
            this.dom = dom;
            this.amount = amount;
            this.holder = holder;
        }

        @Suspendable
        @Override
        public String call() throws FlowException {

            StateAndRef<MicroscopeToken> frameStateandRef = getServiceHub().getVaultService()
                    .queryBy(MicroscopeToken.class).getStates().stream()
                    .filter(sf -> sf.getState().getData().getName().equals(this.name)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException("StockState symbol=\"" + this.name + "\" not found from vault"));

            MicroscopeToken deviceTokenState = frameStateandRef.getState().getData();

            TokenPointer deviceTokenPointer = deviceTokenState.toPointer(deviceTokenState.getClass());

            PartyAndToken partyAndToken = new PartyAndToken(holder, deviceTokenPointer);

            SignedTransaction stx = (SignedTransaction) subFlow(new MoveNonFungibleTokens(partyAndToken));


            return "\nTransfer ownership of a device name: "+ this.name + " to "
                    + this.holder.getName().getOrganisation() + "\nTransaction IDs: "
                    + stx.getId();
        }
    }


    @InitiatedBy(TransferDeviceFlow.TransferDeviceToken.class)
    public static class TransferDeviceTokenResponder extends FlowLogic<Unit>{

        private FlowSession counterSession;

        public TransferDeviceTokenResponder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {

            return subFlow(new MoveNonFungibleTokensHandler(counterSession));

        }
    }

}
