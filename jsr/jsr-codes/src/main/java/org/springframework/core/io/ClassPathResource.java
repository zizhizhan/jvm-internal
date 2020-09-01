package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ClassPathResource extends AbstractFileResolvingResource {

	private final String path;
	private ClassLoader classLoader;
	private Class<?> clazz;

	public ClassPathResource(String path) {
		this(path, (ClassLoader) null);
	}

	public ClassPathResource(String path, ClassLoader classLoader) {
		Assert.notNull(path, "Path must not be null");
		String pathToUse = StringUtils.cleanPath(path);
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		this.path = pathToUse;
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}

	public ClassPathResource(String path, Class<?> clazz) {
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.clazz = clazz;
	}

	protected ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
		this.path = StringUtils.cleanPath(path);
		this.classLoader = classLoader;
		this.clazz = clazz;
	}

	public final String getPath() {
		return this.path;
	}

	public final ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : this.clazz.getClassLoader());
	}

	@Override
	public boolean exists() {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		return (url != null);
	}

	public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		} else {
			is = this.classLoader.getResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
		}
		return is;
	}

	@Override
	public URL getURL() throws IOException {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		if (url == null) {
			throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return new ClassPathResource(pathToUse, this.classLoader, this.clazz);
	}

	@Override
	public String getFilename() {
		return StringUtils.getFilename(this.path);
	}

	public String getDescription() {
		StringBuilder builder = new StringBuilder("class path resource [");

		if (this.clazz != null) {
			builder.append(ClassUtils.classPackageAsResourcePath(this.clazz));
			builder.append('/');
		}

		builder.append(this.path);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ClassPathResource) {
			ClassPathResource otherRes = (ClassPathResource) obj;
			return (this.path.equals(otherRes.path)
					&& ObjectUtils.nullSafeEquals(this.classLoader, otherRes.classLoader) && ObjectUtils
					.nullSafeEquals(this.clazz, otherRes.clazz));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
