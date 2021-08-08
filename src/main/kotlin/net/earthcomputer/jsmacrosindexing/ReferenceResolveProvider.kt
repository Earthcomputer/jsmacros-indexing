package net.earthcomputer.jsmacrosindexing

import com.intellij.psi.JavaPsiFacade
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.resolve.PyReferenceResolveProvider
import com.jetbrains.python.psi.resolve.RatedResolveResult
import com.jetbrains.python.psi.types.TypeEvalContext

class ReferenceResolveProvider : PyReferenceResolveProvider {
    override fun resolveName(p0: PyQualifiedExpression, p1: TypeEvalContext): MutableList<RatedResolveResult> {
        val name = p0.referencedName ?: return mutableListOf()
        when (name) {
            "context" -> {
                val facade = JavaPsiFacade.getInstance(p0.project)
                val clazz = facade.findClass("xyz.wagyourtail.jsmacros.core.language.EventContainer", p0.resolveScope)
                    ?: facade.findClass("xyz.wagyourtail.jsmacros.core.language.ContextContainer", p0.resolveScope)
                    ?: return mutableListOf()
                return mutableListOf(RatedResolveResult(RatedResolveResult.RATE_LOW, clazz))
            }
            "event" -> {
                val facade = JavaPsiFacade.getInstance(p0.project)
                val clazz = facade.findClass("xyz.wagyourtail.jsmacros.core.event.BaseEvent", p0.resolveScope)
                    ?: return mutableListOf()
                return mutableListOf(RatedResolveResult(RatedResolveResult.RATE_LOW, clazz))
            }
            "file" -> {
                val facade = JavaPsiFacade.getInstance(p0.project)
                val clazz = facade.findClass("java.io.File", p0.resolveScope)
                    ?: return mutableListOf()
                return mutableListOf(RatedResolveResult(RatedResolveResult.RATE_LOW, clazz))
            }
            else -> return ReferenceContributor.resolveLibraryClasses(p0.project, name)
                .map { RatedResolveResult(RatedResolveResult.RATE_NORMAL, it) }
                .toMutableList()
        }
    }
}
