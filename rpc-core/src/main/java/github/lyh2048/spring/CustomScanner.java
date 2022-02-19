package github.lyh2048.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

public class CustomScanner extends ClassPathBeanDefinitionScanner {
    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotationType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
    }

    @Override
    public int scan(@Nonnull String... basePackages) {
        return super.scan(basePackages);
    }
}
