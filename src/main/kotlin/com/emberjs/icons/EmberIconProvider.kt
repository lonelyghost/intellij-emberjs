package com.emberjs.icons

import com.emberjs.project.EmberProjectComponent
import com.emberjs.resolver.EmberName
import com.emberjs.utils.guessProject
import com.intellij.icons.AllIcons
import com.intellij.ide.IconProvider
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vfs.VfsUtil.isAncestor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.LayeredIcon
import javax.swing.Icon

class EmberIconProvider : IconProvider() {

    override fun getIcon(element: PsiElement, @Iconable.IconFlags flags: Int) = getIcon(element.containingFile)

    fun getIcon(psiFile: PsiFile?) = psiFile?.virtualFile?.let { getIcon(it) }

    fun getIcon(file: VirtualFile): Icon? {
        if (file.extension != "js" && file.extension != "hbs")
            return null

        val project = file.guessProject() ?: return null
        val roots = EmberProjectComponent.getInstance(project)?.roots ?: return null

        return roots.filter { isAncestor(it, file, true) }
                .map { root -> EmberName.from(root, file)?.let { getIcon(it) } }
                .filterNotNull()
                .firstOrNull()
    }

    fun getIcon(name: EmberName) = getIcon(name.type)

    companion object {
        fun getIcon(type: String): Icon? {
            val typeWithoutSuffix = type.removeSuffix("-test").removeSuffix("-integration")
            val baseIcon = EmberIcons.FILE_TYPE_ICONS[typeWithoutSuffix]

            return when {
                baseIcon == null -> null
                type.endsWith("-test") -> LayeredIcon(baseIcon, AllIcons.Nodes.JunitTestMark)
                else -> baseIcon
            }
        }
    }
}
