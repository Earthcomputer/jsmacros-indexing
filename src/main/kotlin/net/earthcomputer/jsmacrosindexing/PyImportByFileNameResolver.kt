package net.earthcomputer.jsmacrosindexing

import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.impl.PyImportResolver
import com.jetbrains.python.psi.resolve.PyQualifiedNameResolveContext

class PyImportByFileNameResolver : PyImportResolver {
    override fun resolveImportReference(
        qName: QualifiedName,
        context: PyQualifiedNameResolveContext,
        p2: Boolean
    ): PsiElement? {
        val directoryParts = qName.components.dropLast(1)
        val fileName = (qName.lastComponent ?: return null) + ".py"
        val footholdFile = context.footholdFile ?: return null
        var dir = footholdFile.containingDirectory
        while (dir != null) {
            var child = dir
            for (dirPart in directoryParts) {
                child = child.findSubdirectory(dirPart)
                if (child == null) {
                    break
                }
            }
            if (child != null) {
                val pyFile = child.findFile(fileName)
                if (pyFile is PyFile) {
                    return pyFile
                }
            }
            dir = dir.parentDirectory
        }

        return null
    }
}
