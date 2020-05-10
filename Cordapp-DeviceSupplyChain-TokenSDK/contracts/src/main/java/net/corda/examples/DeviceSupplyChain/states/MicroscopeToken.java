package net.corda.examples.DeviceSupplyChain.states;

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.examples.DeviceSupplyChain.contracts.MicroscopeContract;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(MicroscopeContract.class)
public class MicroscopeToken extends EvolvableTokenType {

    private final String name;
    private final String batchno;
    private final int dom;
    private final int amount;

    private final Party maintainer;
    private final UniqueIdentifier uniqueIdentifier;
    private final int fractionDigits;

    public MicroscopeToken(String name, String batchno, int dom, int amount, Party maintainer, UniqueIdentifier uniqueIdentifier, int fractionDigits) {
        this.name = name;
        this.batchno = batchno;
        this.dom = dom;
        this.amount = amount;
        this.maintainer = maintainer;
        this.uniqueIdentifier = uniqueIdentifier;
        this.fractionDigits = fractionDigits;
    }

    public Party getIssuer() { return this.maintainer; }

    public String getName() {
        return name;
    }

    public String getBatchno() {
        return batchno;
    }

    public int getDom() {
        return dom;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getFractionDigits() {
        return this.fractionDigits;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return Arrays.asList(maintainer);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return uniqueIdentifier;
    }
}
