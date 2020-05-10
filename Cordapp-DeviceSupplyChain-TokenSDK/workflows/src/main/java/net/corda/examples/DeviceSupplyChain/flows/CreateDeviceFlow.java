package net.corda.examples.DeviceSupplyChain.flows;

import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.examples.DeviceSupplyChain.states.MicroscopeToken;

@StartableByRPC
public class CreateDeviceFlow extends FlowLogic<String> {

    private final String name;
    private final String batchno;
    private final int dom;
    private final int amount;

    public CreateDeviceFlow(String name, String batchno, int dom, int amount) {
        this.name = name;
        this.batchno = batchno;
        this.dom = dom;
        this.amount = amount;
    }


    @Override
    public String call() throws FlowException {

        //Getting the notary
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Create non-fungible TabletToken
        UniqueIdentifier uuid = new UniqueIdentifier();
        MicroscopeToken microscopeToken = new MicroscopeToken(this.name, this.batchno, this.dom, this.amount, getOurIdentity(), uuid, 0);

        TransactionState transactionState = new TransactionState(microscopeToken, notary);

        //Calling subflow to create Evolvable Token
        subFlow(new CreateEvolvableTokens(transactionState));

        return "\nA new device is being built of batch Number " + this.batchno;
    }

}
