package net.earthcomputer.jsmacrosindexing

import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.InheritanceUtil
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.resolve.PyReferenceResolveProvider
import com.jetbrains.python.psi.resolve.RatedResolveResult
import com.jetbrains.python.psi.types.TypeEvalContext

class ReferenceResolveProvider : PyReferenceResolveProvider {
    override fun resolveName(p0: PyQualifiedExpression, p1: TypeEvalContext): MutableList<RatedResolveResult> {
        val name = p0.referencedName ?: return mutableListOf()
        val baseLibraryClass =
            JavaPsiFacade.getInstance(p0.project)
                .findClass("xyz.wagyourtail.jsmacros.core.library.BaseLibrary", GlobalSearchScope.allScope(p0.project))
                ?: return mutableListOf()
        val pyLanguageClass =
            JavaPsiFacade.getInstance(p0.project)
                .findClass("xyz.wagyourtail.jsmacros.jython.language.impl.JythonLanguageDescription", GlobalSearchScope.allScope(p0.project))
                ?: return mutableListOf()
        return ClassInheritorsSearch.search(baseLibraryClass).findAll()
            .filter { clazz ->
                val annotation = clazz.getAnnotation("xyz.wagyourtail.jsmacros.core.library.Library") ?: return@filter false
                if ((annotation.findAttributeValue("value") as? PsiLiteralValue)?.value != name) {
                    return@filter false
                }
                val languages = annotation.findAttributeValue("languages") ?: return@filter true
                return@filter when (languages) {
                    is PsiClassObjectAccessExpression -> {
                        val libClass = languages.operand.innermostComponentReferenceElement?.resolve() as? PsiClass ?: return@filter false
                        InheritanceUtil.isInheritorOrSelf(libClass, pyLanguageClass, true)
                    }
                    is PsiArrayInitializerMemberValue -> {
                        if (languages.initializers.isEmpty()) {
                            return@filter true
                        }
                        languages.initializers.filterIsInstance(PsiClassObjectAccessExpression::class.java).any { objectAccess ->
                            val libClass = objectAccess.operand.innermostComponentReferenceElement?.resolve() as? PsiClass ?: return@filter false
                            InheritanceUtil.isInheritorOrSelf(libClass, pyLanguageClass, true)
                        }
                    }
                    else -> false
                }
            }
            .map { clazz ->
                RatedResolveResult(RatedResolveResult.RATE_NORMAL, clazz)
            }
            .toMutableList()
    }
}
