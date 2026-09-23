import re
import unicodedata

# --------------------------------------------------------------------------
# Deterministic Tamil Unicode -> ASCII romanizer.
#
# WHY THIS EXISTS:
# The previous version delegated Tamil-script -> roman conversion to an
# external library (open-tamil's txt2roman / encode_string). That library's
# romanization scheme does NOT line up with normalize_phonetic_key's ASCII
# collapsing rules ('th'->'t', 'nd'->'n', 'zh'->'l', duplicate-consonant
# collapse). Those rules were written to match how a *user* types Tamil in
# roman letters (e.g. "uTkaruttu" / "utkaruthu"), not how the external
# library spells things (which may use diacritics like ṭ, ḻ, ṉ, or a
# totally different ASCII split). Result: romanized input for a word
# normalized fine, but the same word typed in native Tamil script produced
# a different, unmatched canonical key.
#
# FIX: transliterate straight from Tamil Unicode codepoints to the exact
# ASCII shapes normalize_phonetic_key already knows how to collapse
# (dental த -> "th", retroflex ட -> "t", ழ -> "zh", etc.), so script input
# and romanized input always converge on the same key.
# --------------------------------------------------------------------------

PULLI = '\u0BCD'  # ்  (virama - marks a "bare" consonant, no vowel)

INDEPENDENT_VOWELS = {
    '\u0B85': 'a', '\u0B86': 'aa', '\u0B87': 'i', '\u0B88': 'ii',
    '\u0B89': 'u', '\u0B8A': 'uu', '\u0B8E': 'e', '\u0B8F': 'ee',
    '\u0B90': 'ai', '\u0B92': 'o', '\u0B93': 'oo', '\u0B94': 'au',
}

VOWEL_SIGNS = {
    '\u0BBE': 'aa', '\u0BBF': 'i', '\u0BC0': 'ii', '\u0BC1': 'u',
    '\u0BC2': 'uu', '\u0BC6': 'e', '\u0BC7': 'ee', '\u0BC8': 'ai',
    '\u0BCA': 'o', '\u0BCB': 'oo', '\u0BCC': 'au',
}

# Base consonant sounds, deliberately chosen to match normalize_phonetic_key's
# collapsing rules rather than any "standard" transliteration scheme.
CONSONANTS = {
    '\u0B95': 'k',   # க
    '\u0B99': 'ng',  # ங
    '\u0B9A': 'c',   # ச
    '\u0B9E': 'ny',  # ஞ
    '\u0B9F': 't',   # ட  (retroflex t)
    '\u0BA3': 'n',   # ண  (retroflex n)
    '\u0BA4': 'th',  # த  (dental t -> "th" so th->t rule applies)
    '\u0BA8': 'n',   # ந  (dental n)
    '\u0BAA': 'p',   # ப
    '\u0BAE': 'm',   # ம
    '\u0BAF': 'y',   # ய
    '\u0BB0': 'r',   # ர
    '\u0BB2': 'l',   # ல
    '\u0BB5': 'v',   # வ
    '\u0BB4': 'zh',  # ழ  (matches existing zh->l rule)
    '\u0BB3': 'l',   # ள
    '\u0BB1': 'r',   # ற  (alveolar trill)
    '\u0BA9': 'n',   # ன
    # Grantha / loan letters
    '\u0B9C': 'j',   # ஜ
    '\u0BB7': 'sh',  # ஷ
    '\u0BB8': 's',   # ஸ
    '\u0BB9': 'h',   # ஹ
    '\u0BB6': 'sh',  # ஶ
}


def tamil_script_to_roman(word: str) -> str:
    """
    Converts native Tamil script to an ASCII romanization whose shape is
    compatible with normalize_phonetic_key (th/zh/duplicate-consonant
    handling). Replaces the old external-library transliteration step.
    """
    out = []
    i = 0
    n = len(word)
    while i < n:
        ch = word[i]
        if ch in CONSONANTS:
            base = CONSONANTS[ch]
            nxt = word[i + 1] if i + 1 < n else ''
            if nxt == PULLI:
                out.append(base)          # bare consonant, no vowel
                i += 2
            elif nxt in VOWEL_SIGNS:
                out.append(base + VOWEL_SIGNS[nxt])
                i += 2
            else:
                out.append(base + 'a')    # inherent 'a' vowel
                i += 1
        elif ch in INDEPENDENT_VOWELS:
            out.append(INDEPENDENT_VOWELS[ch])
            i += 1
        elif ch == PULLI:
            # Stray pulli with no preceding consonant - skip safely.
            i += 1
        else:
            # Aaytham (ஃ) or anything else unrecognized: skip rather than
            # letting it pass through and corrupt the key.
            i += 1
    return ''.join(out)


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

    # 2. Convert native Tamil script to a normalize_phonetic_key-compatible
    #    romanization (self-contained, no external transliteration library).
    is_tamil_script = any('\u0b80' <= char <= '\u0bff' for char in word)

    if is_tamil_script:
        word = tamil_script_to_roman(word)

    # 3. Apply phonetic normalization key
    return normalize_phonetic_key(str(word))


if __name__ == "__main__":
    # Quick sanity check
    tests = ["uTkaruttu", "utkaruthu", "உட்கருத்து"]
    for t in tests:
        print(f"{t!r:20} -> {get_tamil_lemma(t)!r}")