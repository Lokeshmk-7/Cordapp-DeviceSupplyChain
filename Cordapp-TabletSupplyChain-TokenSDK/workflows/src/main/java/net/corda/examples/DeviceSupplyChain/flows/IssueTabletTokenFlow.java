package net.corda.examples.DeviceSupplyChain.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class IssueTabletTokenFlow extends FlowLogic<String>{

    private final String name;
    private final String batchno;
    private final int dom;
    private final int doe;
    private final int quantity;
    private final int amount;
    private final Party holder;

    public IssueTabletTokenFlow(String name, String batchno, int dom, int doe, int quantity, int amount, Party holder) {
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

        //Get the tablet from the ledger

        StateAndRef<TabletToken> tabletTokenStateAndRef = getServiceHub().getVaultService().
                queryBy(TabletToken.class).getStates().stream()
                .filter(sf->sf.getState().getData().getName().equals(this.name)).findAny()
                .orElseThrow(()-> new IllegalArgumentException("StockState symbol=\""+this.name+"\" not found from vault"));

        //getting token object
        TabletToken tabletTokenObject = tabletTokenStateAndRef.getState().getData();

        //getting the pointer to the frame
        TokenPointer frameTokenPointer = tabletTokenObject.toPointer(tabletTokenObject.getClass());

        //Assigning the issuer to token pointer, who indeed issues the token
        IssuedTokenType frameIssuedTokenType = new IssuedTokenType(getOurIdentity(), frameTokenPointer);

        //Creating nonfungible token
        NonFungibleToken tabletToken = new NonFungibleToken(frameIssuedTokenType, holder, new UniqueIdentifier(), TransactionUtilitiesKt.getAttachmentIdForGenericParam(frameTokenPointer));

        //Issuing the tablets
        SignedTransaction stx = subFlow(new IssueTokens(Arrays.asList(tabletToken)));


        return "\nA new tablet batch is being issued to "+ this.holder.getName().getOrganisation() + " with batch number: "
                + this.batchno + "\nTransaction ID: " + stx.getId();
    }
}
