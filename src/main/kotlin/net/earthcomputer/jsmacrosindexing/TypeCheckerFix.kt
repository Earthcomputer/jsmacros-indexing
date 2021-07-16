package net.earthcomputer.jsmacrosindexing

import com.intellij.psi.util.InheritanceUtil
import com.jetbrains.python.psi.impl.PyJavaClassType
import com.jetbrains.python.psi.types.PyGenericType
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.PyTypeCheckerExtension
import com.jetbrains.python.psi.types.TypeEvalContext
import java.util.*

class TypeCheckerFix : PyTypeCheckerExtension {
    override fun match(
        expected: PyType?,
        actual: PyType?,
        context: TypeEvalContext,
        p3: MutableMap<PyGenericType, PyType>
    ): Optional<Boolean> {
        val expectedClass = (expected as? PyJavaClassType)?.psiClass ?: return Optional.empty()
        val actualClass = (actual as? PyJavaClassType)?.psiClass ?: return Optional.empty()
        return Optional.of(InheritanceUtil.isInheritorOrSelf(actualClass, expectedClass, true))
    }
}
