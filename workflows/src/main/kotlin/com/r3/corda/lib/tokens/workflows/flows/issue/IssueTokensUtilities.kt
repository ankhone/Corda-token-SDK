package com.r3.corda.lib.tokens.workflows.flows.issue

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.states.AbstractToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder

/**
 * A function that adds a list of output [AbstractToken] states to a [TransactionBuilder]. It automatically adds
 * [IssueTokenCommand] commands for each [IssuedTokenType]. A notary [Party] must be added to the [TransactionBuilder]
 * before this function can be called.
 */
@Suspendable
fun addIssueTokens(transactionBuilder: TransactionBuilder, outputs: List<AbstractToken<*>>): TransactionBuilder {
    val outputGroups: Map<IssuedTokenType<TokenType>, List<AbstractToken<*>>> = outputs.groupBy { it.issuedTokenType }
    return transactionBuilder.apply {
        outputGroups.forEach { (issuedTokenType: IssuedTokenType<TokenType>, states: List<AbstractToken<*>>) ->
            val issuers = states.map { it.issuer }.toSet()
            require(issuers.size == 1) { "All tokensToIssue must have the same issuer." }
            val issuer = issuers.single()
            addCommand(IssueTokenCommand(issuedTokenType), issuer.owningKey)
            states.forEach { state -> addOutputState(state) }
        }
    }
}

/**
 * A function that adds a list of output [AbstractToken] states to a [TransactionBuilder]. It automatically adds
 * [IssueTokenCommand] commands for each [IssuedTokenType]. A notary [Party] must be added to the [TransactionBuilder]
 * before this function can be called.
 */
@Suspendable
fun addIssueTokens(transactionBuilder: TransactionBuilder, vararg outputs: AbstractToken<*>): TransactionBuilder {
    return addIssueTokens(transactionBuilder, outputs.toList())
}

/**
 * A function that adds a single output [AbstractToken] state to a [TransactionBuilder]. It automatically adds an
 * [IssueTokenCommand] command. A notary [Party] must be added to the [TransactionBuilder] before this function can be
 * called.
 */
@Suspendable
fun addIssueTokens(transactionBuilder: TransactionBuilder, output: AbstractToken<*>): TransactionBuilder {
    return addIssueTokens(transactionBuilder, listOf(output))
}