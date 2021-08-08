package net.earthcomputer.jsmacrosindexing

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import com.jetbrains.python.psi.impl.PyImportResolver
import com.jetbrains.python.psi.resolve.PyQualifiedNameResolveContext

class JavaImportFixResolver : PyImportResolver {
    override fun resolveImportReference(
        qName: QualifiedName,
        context: PyQualifiedNameResolveContext,
        p2: Boolean
    ): PsiElement? {
        return JavaPsiFacade.getInstance(context.project).findPackage(qName.toString())
    }
}
