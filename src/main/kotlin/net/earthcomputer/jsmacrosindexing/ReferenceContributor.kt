package net.earthcomputer.jsmacrosindexing

import com.intellij.openapi.project.Project
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.InheritanceUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.resolve.RatedResolveResult

class ReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PyQualifiedExpression::class.java), object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReferenceBase<PyQualifiedExpression>> {
                println("Resolving psi reference element ${(element as PyQualifiedExpression).referencedName}")
                return arrayOf(
                    object : PsiReferenceBase.Poly<PyQualifiedExpression>(element as PyQualifiedExpression) {
                        override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
                            val name = myElement.referencedName ?: return emptyArray()
                            return resolveLibraryClasses(myElement.project, name)
                                .map {
                                    println("Resolved ${it.qualifiedName}")
                                    RatedResolveResult(RatedResolveResult.RATE_NORMAL, it)
                                }
                                .toTypedArray()
                        }
                    })
            }
        })
    }

    companion object {
        fun resolveLibraryClasses(project: Project, name: String): List<PsiClass> {
            val baseLibraryClass =
                JavaPsiFacade.getInstance(project)
                    .findClass("xyz.wagyourtail.jsmacros.core.library.BaseLibrary", GlobalSearchScope.allScope(project))
                    ?: return mutableListOf()
            val pyLanguageClass =
                JavaPsiFacade.getInstance(project)
                    .findClass("xyz.wagyourtail.jsmacros.jython.language.impl.JythonLanguageDescription", GlobalSearchScope.allScope(project))
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
                                val libClass = (objectAccess.operand.type as? PsiClassType)?.resolve() ?: return@filter false
                                InheritanceUtil.isInheritorOrSelf(libClass, pyLanguageClass, true)
                            }
                        }
                        else -> false
                    }
                }
        }
    }
}
