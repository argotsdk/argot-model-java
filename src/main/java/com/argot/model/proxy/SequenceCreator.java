package com.argot.model.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

/**
 * Based on a MetaSequence generates a class.
 * @author davidryan
 *
 */

public class SequenceCreator implements Opcodes {
    public static byte[] getBytecode(final TypeLibrary library, final TypeElement element, final Class<?>... extendedInterfaces)
    throws TypeException
    {

        if (!(element instanceof MetaSequence)) {
            throw new TypeException("type not defined by sequence");
        }

        final MetaSequence sequence = (MetaSequence) element;
        final MetaName name = library.getName(sequence.getMemberTypeId());
        final ClassWriter cw = new ClassWriter(0);
        final String[] interfaces = new String[extendedInterfaces.length];

        int i = 0;

        for (final Class<?> interfac : extendedInterfaces) {
            interfaces[i] = interfac.getName().replace('.', '/');
            i++;
        }

        // Generate the class header.
        cw.visit(V1_6, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                name.getFullName(), null, "java/lang/Object", interfaces);

        for (int x=0; x<sequence.size(); x++) {
            final TypeElement attrElement = sequence.getElement(x);
            if (!(attrElement instanceof MetaTag)) {
                throw new TypeException("Sequence elements must be defined with Tag");
            }

            final MetaTag tag = (MetaTag) attrElement;
            final TypeElement typeElement = tag.getExpression();
            if (!(typeElement instanceof MetaReference)) {
                throw new TypeException("Tag elements must reference type");
            }
            final MetaReference reference = (MetaReference) typeElement;
            final TypeElement e =  library.getStructure(reference.getType());

            final MetaIdentity identity = (MetaIdentity) e;
            final Integer[] versions = identity.getVersionIdentifiers();
            final Class clss = library.getClass(versions[0]);
            //final Class clss = library.getClass(reference.getType());

            final String tagName = tag.getDescription();

            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, getSetterName(tagName), Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(clss)}), null, null)
            .visitAnnotation("L"+Type.getType(ModelAnnotation.class).getInternalName()+";", true).visit("value", x);
            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, getGetterName(tagName), Type.getMethodDescriptor(Type.getType(clss), new Type[] {}), null, null)
            .visitAnnotation("L" + Type.getType(ModelAnnotation.class).getInternalName()+";", true).visit("value", x);
            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, getHasChangedName(tagName), Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] {}), null, null)
            .visitAnnotation("L"+Type.getType(ModelAnnotation.class).getInternalName()+";", true).visit("value", x);

        }

/*
        final ArrayList<String> exceptions = new ArrayList<String>();
        for (final Method m : methods) {
            exceptions.clear();
            for (final Class<?> exception : m.getExceptionTypes()) {
                exceptions.add(getInternalNameOf(exception));
            }
            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, m.getName(), getMethodDescriptorOf(m), null, null);
        }
        */
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static String getGetterName(final String name) {
        return "get" + name;
    }

    private static String getSetterName(final String name) {
        return "set" + name;
    }

    private static String getHasChangedName(final String name) {
        return "has" + name + "Changed";
    }

}
