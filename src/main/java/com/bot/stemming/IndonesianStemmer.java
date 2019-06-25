package com.bot.stemming;


public class IndonesianStemmer {
	
	private int numSyllables;
	  private int flags;
	  private static final int REMOVED_KE = 1;
	  private static final int REMOVED_PENG = 2;
	  private static final int REMOVED_DI = 4;
	  private static final int REMOVED_MENG = 8;
	  private static final int REMOVED_TER = 16;
	  private static final int REMOVED_BER = 32;
	  private static final int REMOVED_PE = 64;
	  
	  /**
	   * Stem a term (returning its new length).
	   * <p>
	   * Use <code>stemDerivational</code> to control whether full stemming
	   * or only light inflectional stemming is done.
	   */
	  public int stem(char text[], int length, boolean stemDerivational) {
	    flags = 0;
	    numSyllables = 0;
	    for (int i = 0; i < length; i++)
	      if (isVowel(text[i]))
	          numSyllables++;
	    
	    if (numSyllables > 2) length = removeParticle(text, length);
	    if (numSyllables > 2) length = removePossessivePronoun(text, length);
	    
	    if (stemDerivational)
	      length = stemDerivational(text, length);
	    return length;
	  }
	  
	  private int stemDerivational(char text[], int length) {
	    int oldLength = length;
	    if (numSyllables > 2) length = removeFirstOrderPrefix(text, length);
	    if (oldLength != length) { // a rule is fired
	      oldLength = length;
	      if (numSyllables > 2) length = removeSuffix(text, length);
	      if (oldLength != length) // a rule is fired
	        if (numSyllables > 2) length = removeSecondOrderPrefix(text, length);
	    } else { // fail
	      if (numSyllables > 2) length = removeSecondOrderPrefix(text, length);
	      if (numSyllables > 2) length = removeSuffix(text, length);
	    }
	    return length;
	  }
	  
	  private boolean isVowel(char ch) {
	    switch(ch) {
	      case 'a':
	      case 'e':
	      case 'i':
	      case 'o':
	      case 'u':
	        return true;
	      default:
	        return false;
	    }
	  }
	  
	  private int removeParticle(char text[], int length) {
	    if (StemmerUtil.endsWith(text, length, "kah") || 
	        StemmerUtil.endsWith(text, length, "lah") || 
	        StemmerUtil.endsWith(text, length, "pun")) {
	        numSyllables--;
	        return length - 3;
	    }
	    
	    return length;
	  }
	  
	  private int removePossessivePronoun(char text[], int length) {
	    if (StemmerUtil.endsWith(text, length, "ku") || StemmerUtil.endsWith(text, length, "mu")) {
	      numSyllables--;
	      return length - 2;
	    }
	    
	    if (StemmerUtil.endsWith(text, length, "nya")) {
	      numSyllables--;
	      return length - 3;
	    }
	    
	    return length;
	  }
	  
	  private int removeFirstOrderPrefix(char text[], int length) {
	    if (StemmerUtil.startsWith(text, length, "meng")) {
	      flags |= REMOVED_MENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 4);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "meny") && length > 4 && isVowel(text[4])) {
	      flags |= REMOVED_MENG;
	      text[3] = 's';
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "men")) {
	      flags |= REMOVED_MENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	 
	    if (StemmerUtil.startsWith(text, length, "mem")) {
	      flags |= REMOVED_MENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "me")) {
	      flags |= REMOVED_MENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "peng")) {
	      flags |= REMOVED_PENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 4);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "peny") && length > 4 && isVowel(text[4])) {
	      flags |= REMOVED_PENG;
	      text[3] = 's';
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "peny")) {
	      flags |= REMOVED_PENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 4);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "pen") && length > 3 && isVowel(text[3])) {
	      flags |= REMOVED_PENG;
	      text[2] = 't';
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }

	    if (StemmerUtil.startsWith(text, length, "pen")) {
	      flags |= REMOVED_PENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "pem")) {
	      flags |= REMOVED_PENG;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "di")) {
	      flags |= REMOVED_DI;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "ter")) {
	      flags |= REMOVED_TER;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "ke")) {
	      flags |= REMOVED_KE;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }
	    
	    return length;
	  }
	  
	  private int removeSecondOrderPrefix(char text[], int length) {
	    if (StemmerUtil.startsWith(text, length, "ber")) {
	      flags |= REMOVED_BER;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (length == 7 && StemmerUtil.startsWith(text, length, "belajar")) {
	      flags |= REMOVED_BER;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "be") && length > 4 
	        && !isVowel(text[2]) && text[3] == 'e' && text[4] == 'r') {
	      flags |= REMOVED_BER;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "per")) {
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (length == 7 && StemmerUtil.startsWith(text, length, "pelajar")) {
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 3);
	    }
	    
	    if (StemmerUtil.startsWith(text, length, "pe")) {
	      flags |= REMOVED_PE;
	      numSyllables--;
	      return StemmerUtil.deleteN(text, 0, length, 2);
	    }

	    return length;
	  }
	  
	  private int removeSuffix(char text[], int length) {
	    if (StemmerUtil.endsWith(text, length, "kan") 
	        && (flags & REMOVED_KE) == 0 
	        && (flags & REMOVED_PENG) == 0 
	        && (flags & REMOVED_PE) == 0) {
	      numSyllables--;
	      return length - 3;
	    }
	    
	    if (StemmerUtil.endsWith(text, length, "an") 
	        && (flags & REMOVED_DI) == 0 
	        && (flags & REMOVED_MENG) == 0 
	        && (flags & REMOVED_TER) == 0) {
	      numSyllables--;
	      return length - 2;
	    }
	    
	    if (StemmerUtil.endsWith(text, length, "i") 
	        && !StemmerUtil.endsWith(text, length, "si") 
	        && (flags & REMOVED_BER) == 0 
	        && (flags & REMOVED_KE) == 0 
	        && (flags & REMOVED_PENG) == 0) {
	      numSyllables--;
	      return length - 1;
	    }
	    return length;
	  }  
	  
	  public static void main(String[] args) {
		  	IndonesianStemmer indoStemmer = new IndonesianStemmer();
		 
		 // 
			String testString = "berenang";
			char[] stringToCharArray = testString.toCharArray();
	 
		/*	for (char output : stringToCharArray) {
				System.out.println(output);
			}
			*/
			
			int len = indoStemmer.stem(stringToCharArray, stringToCharArray.length, true);
			String stem = new String(stringToCharArray, 0, len);
			System.out.println(stem);
	  }

}
