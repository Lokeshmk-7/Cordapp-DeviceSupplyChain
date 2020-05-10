package net.corda.examples.DeviceSupplyChain.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokensHandler;
import kotlin.Unit;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.examples.DeviceSupplyChain.states.MicroscopeToken;

public class RecallDeviceFlow {

    public RecallDeviceFlow() {
    }

    @InitiatingFlow
    @StartableByRPC
    public static class RecallDevice extends FlowLogic<String>{

        private String name;

        public RecallDevice(String name) {
            this.name = name;
        }

        @Suspendable
        @Override
        public String call() throws FlowException {

            StateAndRef<MicroscopeToken> frameStateAndRef = getServiceHub().getVaultService()
                    .queryBy(MicroscopeToken.class).getStates().stream()
                    .filter(sf -> sf.getState().getData().getName().equals(this.name)).findAny()
                    .orElseThrow(()->new IllegalArgumentException("StockState symbol=\"" + this.name + "\" not found from vault"));

            MicroscopeToken deviceToken = frameStateAndRef.getState().getData();
            Party issuer = deviceToken.getIssuer();

            TokenPointer tokenPointer = deviceToken.toPointer(deviceToken.getClass());

            SignedTransaction st = subFlow(new RedeemNonFungibleTokens(tokenPointer, issuer));

            return "\nThe device is totaled, and the token is redeem to PharmaCo" + "\nTransaction ID: " + st.getId();
        }
    }


    @InitiatedBy(RecallDevice.class)
    public static class RecallDeviceResponder extends FlowLogic<Unit>{

        private FlowSession counterSession;

        public RecallDeviceResponder(FlowSession counterSession) {
            counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {

            return subFlow(new RedeemNonFungibleTokensHandler(counterSession));
        }
    }


}
