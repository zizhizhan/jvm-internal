package com.zizhizhan.legacies.scan.spi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.zizhizhan.legacies.scan.util.ReflectionHelper;
import com.zizhizhan.legacies.scan.resource.ScannerListener;


public class AnnotationScannerListener implements ScannerListener {

	private final ClassLoader classloader;

    private final Set<Class<?>> classes;

    private final Set<String> annotations;
    
    private final AnnotatedClassVisitor classVisitor;

  
    public AnnotationScannerListener(Class<? extends Annotation>... annotations) {
        this(ReflectionHelper.getContextClassLoader(), annotations);
    }

    public AnnotationScannerListener(ClassLoader classloader,
            Class<? extends Annotation>... annotations) {
        this.classloader = classloader;
        this.classes = new LinkedHashSet<Class<?>>();
        this.annotations = getAnnotationSet(annotations);
        this.classVisitor = new AnnotatedClassVisitor();
    }

  
    public Set<Class<?>> getAnnotatedClasses() {
        return classes;
    }
    
    private Set<String> getAnnotationSet(Class<? extends Annotation>... annotations) {
        Set<String> a = new HashSet<String>();
        for (Class<?> c : annotations) {
            a.add("L" + c.getName().replaceAll("\\.", "/") + ";");
        }
        return a;
    }

    public boolean onAccept(String name) {
        return name.endsWith(".class");
    }

    public void onProcess(String name, InputStream in) throws IOException {
        new ClassReader(in).accept(classVisitor, 0);
    }

    private final class AnnotatedClassVisitor extends ClassVisitor {

        private String className;
        
        private boolean isScoped;
        
        private boolean isAnnotated;

        public AnnotatedClassVisitor() {
            super(Opcodes.ASM8);
        }

        public void visit(int version, int access, String name,
                String signature, String superName, String[] interfaces) {
            className = name;
            isScoped = (access & Opcodes.ACC_PUBLIC) != 0;
            isAnnotated = false;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            isAnnotated |= annotations.contains(desc);
            return null;
        }

        public void visitInnerClass(String name, String outerName,
                String innerName, int access) {
       
            if (className.equals(name)) {
                isScoped = (access & Opcodes.ACC_PUBLIC) != 0;

                // Inner classes need to be statically scoped
                isScoped &= (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            }
        }

        public void visitEnd() {
            if (isScoped && isAnnotated) {              
                classes.add(getClassForName(className.replaceAll("/", ".")));
            }
        }

        public void visitOuterClass(String string, String string0,
                String string1) {
                   }

        public FieldVisitor visitField(int i, String string,
                String string0, String string1, Object object) {
            
            return null;
        }

        public void visitSource(String string, String string0) {
           
        }

        public void visitAttribute(Attribute attribute) {
            
        }

        public MethodVisitor visitMethod(int i, String string,
                String string0, String string1, String[] string2) {
            
            return null;
        }

        private Class<?> getClassForName(String className) {
            try {
                return ReflectionHelper.classForNameWithException(className, classloader);
            } catch (ClassNotFoundException ex) {
                String s = "A class file of the class name, " + className +
                        "is identified but the class could not be found";
                throw new RuntimeException(s, ex);
            }
        }

    };

}
