import re
import unicodedata

# Safe open-tamil transliteration import across different package versions
tamil2roman = None

try:
    import tamil
    if hasattr(tamil, "txt2roman") and hasattr(tamil.txt2roman, "txt2roman"):
        tamil2roman = tamil.txt2roman.txt2roman
    elif hasattr(tamil, "txt2roman") and hasattr(tamil.txt2roman, "encode_string"):
        tamil2roman = tamil.txt2roman.encode_string
except ImportError:
    try:
        from tamil.txt2roman import encode_string as tamil2roman
    except ImportError:
        try:
            import open_tamil.txt2roman as txt2roman
            tamil2roman = getattr(txt2roman, "txt2roman", getattr(txt2roman, "encode_string", None))
        except ImportError:
            tamil2roman = None

TAMIL_SUFFIXES = [
    "களுக்கு", "இலிருந்து", "களோடு", "களால்", "களின்", 
    "களை", "கள்", "உடைய", "இடம்", "உடன்", "ஆல்", 
    "க்கு", "இல்", "ஓடு", "ஐ"
]

def normalize_tamil(text: str) -> str:
    """Normalizes input string by stripping whitespace and zero-width joiners."""
    if not text:
        return ""
    text = unicodedata.normalize("NFC", text.strip())
    return text.replace("\u200b", "").replace("\u200c", "")

def normalize_phonetic_key(text: str) -> str:
    """
    Collapses phonetic variations (case, retroflex T/t, th/t, double consonants)
    into a single canonical lookup key.
    
    Examples:
        'uTkaruttu' -> 'utkarutu'
        'utkaruthu'  -> 'utkarutu'
    """
    if not text:
        return ""
    
    # 1. Lowercase and remove punctuation/separators
    key = text.lower().strip()
    key = re.sub(r'[\s_\-]+', '', key)
    
    # 2. Standardize common Tamil romanization variations
    key = key.replace('th', 't')   # Maps dental/retroflex variations to 't'
    key = key.replace('nd', 'n')   # Maps 'nd' (ந்) to 'n'
    key = key.replace('zh', 'l')   # Maps 'zh' (ழ) to 'l'
    
    # 3. Collapse duplicate/geminate consonants ('tt' -> 't', 'pp' -> 'p')
    key = re.sub(r'([a-z])\1+', r'\1', key)
    
    return key

def get_tamil_lemma(word: str) -> str:
    """
    Lemmatizes Tamil words, transliterates Tamil Unicode script to Romanized text,
    and applies phonetic key normalization so all spelling variants match.
    """
    word = normalize_tamil(word)
    
    if not word:
        return ""
    
    # 1. Strip inflectional suffixes if word is native Tamil script
    for suffix in TAMIL_SUFFIXES:
        if word.endswith(suffix) and len(word) > len(suffix) + 1:
            root = word[:-len(suffix)]
            if suffix in ["கள்", "களை", "களுக்கு"] and root.endswith("ங்"):
                root = root[:-1] + "ம்"
            word = root
            break
            
    # 2. Convert native Tamil script to Romanized string
    is_tamil_script = any('\u0b80' <= char <= '\u0bff' for char in word)
    
    if is_tamil_script and callable(tamil2roman):
        try:
            converted = tamil2roman(word)
            if isinstance(converted, str) and converted:
                word = converted
        except Exception:
            pass
            
    # 3. Apply phonetic normalization key
    return normalize_phonetic_key(str(word))