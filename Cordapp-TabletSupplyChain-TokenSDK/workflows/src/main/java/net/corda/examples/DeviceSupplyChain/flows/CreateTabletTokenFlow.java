package net.corda.examples.DeviceSupplyChain.flows;

import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

@StartableByRPC
public class CreateTabletTokenFlow extends FlowLogic<String>{

    private final String name;
    private final String batchno;
    private final int dom;
    private final int doe;
    private final int quantity;
    private final int amount;

    public CreateTabletTokenFlow(String name, String batchno, int dom, int doe, int quantity, int amount) {
        this.name = name;
        this.batchno = batchno;
        this.dom = dom;
        this.doe = doe;
        this.quantity = quantity;
        this.amount = amount;
    }


    @Override
    public String call() throws FlowException {

        //Getting the notary
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Create non-fungible TabletToken
        UniqueIdentifier uuid = new UniqueIdentifier();
        TabletToken tabletToken = new TabletToken(this.name, this.batchno, this.dom, this.doe, this.quantity, this.amount, getOurIdentity(), uuid, 0);

        TransactionState transactionState = new TransactionState(tabletToken, notary);

        //Calling subflow to create Evolvable Token
        subFlow(new CreateEvolvableTokens(transactionState));

        return "\nA new tablet batch is being built of Batch Number " + this.batchno;
    }
}
