package com.zizhizhan.javacc.parser.calc;

public class Token implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public int kind;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
	public String image;
	public Token next;
	public Token specialToken;

	public Object getValue() {
		return null;
	}

	public Token() {
	}

	public Token(int kind) {
		this(kind, null);
	}

	public Token(int kind, String image) {
		this.kind = kind;
		this.image = image;
	}

	public String toString() {
		return String.format("image: %s, kind: %d, next:%s", image, kind, next);
	}

	public static Token newToken(int ofKind, String image) {
		switch (ofKind) {
		default:
			return new Token(ofKind, image);
		}
	}

	public static Token newToken(int ofKind) {
		return newToken(ofKind, null);
	}

}