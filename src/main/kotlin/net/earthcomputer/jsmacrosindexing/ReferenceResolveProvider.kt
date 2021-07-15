package net.earthcomputer.jsmacrosindexing

import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.resolve.PyReferenceResolveProvider
import com.jetbrains.python.psi.resolve.RatedResolveResult
import com.jetbrains.python.psi.types.TypeEvalContext

class ReferenceResolveProvider : PyReferenceResolveProvider {
    override fun resolveName(p0: PyQualifiedExpression, p1: TypeEvalContext): MutableList<RatedResolveResult> {
        val name = p0.referencedName ?: return mutableListOf()
        return ReferenceContributor.resolveLibraryClasses(p0.project, name)
            .map { RatedResolveResult(RatedResolveResult.RATE_NORMAL, it) }
            .toMutableList()
    }
}
