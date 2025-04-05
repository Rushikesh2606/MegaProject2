import os
import zipfile
import subprocess
import logging

logging.basicConfig(level=logging.INFO)

# Define allowed file extensions and corresponding language keys.
ALLOWED_EXTENSIONS = {
    'py': 'python',
    'js': 'javascript',
    'java': 'java',
    'cpp': 'cpp',
    'c': 'c',
    'rb': 'ruby',
    'php': 'php',
    'go': 'go',
    'ts': 'typescript',
    'xml': 'xml',
    'cs': 'csharp'
}

def allowed_file(filename):
    """Check if file extension is allowed and return the language."""
    if '.' in filename:
        ext = filename.rsplit('.', 1)[1].lower()
        return ext in ALLOWED_EXTENSIONS, ALLOWED_EXTENSIONS.get(ext, None)
    return False, None

def extract_zip(zip_path, extract_to):
    """Extract the ZIP file to the given directory."""
    with zipfile.ZipFile(zip_path, 'r') as zip_ref:
        zip_ref.extractall(extract_to)

# ======== ANALYSIS FUNCTIONS ========

def analyze_python_syntax(code):
    try:
        compile(code, '<string>', 'exec')
        return []
    except SyntaxError as e:
        return [f'Syntax Error: {e}']

def analyze_ruby_syntax(code):
    try:
        subprocess.run(['ruby', '-c'], input=code, text=True, check=True, capture_output=True)
        return []
    except subprocess.CalledProcessError as e:
        return [f'Syntax Error: {e.stderr.strip()}']

def analyze_php_syntax(code):
    try:
        subprocess.run(['php', '-l'], input=code, text=True, check=True, capture_output=True)
        return []
    except subprocess.CalledProcessError as e:
        return [f'Syntax Error: {e.stderr.strip()}']

def analyze_java_syntax(code):
    with open('temp.java', 'w') as f:
        f.write(code)
    try:
        subprocess.run(['javac', 'temp.java'], check=True)
        return []
    except subprocess.CalledProcessError as e:
        return [f'Syntax Error: {e.stderr.strip()}']
    finally:
        os.remove('temp.java')

def analyze_complexity(code):
    return sum(code.count(keyword) for keyword in ['if', 'for', 'while'])

def analyze_function_length(code):
    function_lengths = []
    lines = code.splitlines()
    inside_function = False
    function_start = 0
    for i, line in enumerate(lines):
        if any(keyword in line for keyword in ['def ', 'function ', 'int', 'void', 'public', 'private']):
            if inside_function:
                function_lengths.append(i - function_start)
            inside_function = True
            function_start = i
        elif inside_function and '}' in line:
            function_lengths.append(i - function_start)
            inside_function = False
    return [length for length in function_lengths if length > 50]

def analyze_comments(code):
    lines = code.splitlines()
    comment_lines = [line for line in lines if line.strip().startswith(('#', '//', '/*', '*', '*/'))]
    code_lines = [line for line in lines if not line.strip().startswith(('#', '//', '/*', '*', '*/'))]
    return len(comment_lines) / (len(code_lines) + len(comment_lines)) if code_lines or comment_lines else 0

def analyze_unused_variables(code):
    variables = set()
    lines = code.splitlines()
    for line in lines:
        if '=' in line:
            variable = line.split('=')[0].strip()
            variables.add(variable)
    used_vars = {word for line in lines for word in line.split() if word in variables}
    return list(variables - used_vars)

def analyze_indentation(code):
    lines = code.splitlines()
    return [line for line in lines if line.startswith(' ') and (len(line) - len(line.lstrip())) % 4 != 0]

def analyze_magic_numbers(code):
    lines = code.splitlines()
    magic_numbers = []
    for line in lines:
        for token in line.split():
            try:
                num = int(token)
                if num not in [-1, 0, 1]:
                    magic_numbers.append(num)
            except ValueError:
                pass
    return list(set(magic_numbers))

def analyze_code_duplication(code):
    lines = code.splitlines()
    return len(lines) - len(set(lines))

def calculate_weights(issues):
    weights = {
        "syntax": 30,
        "complexity": 15,
        "function_length": 10,
        "comment_density": 10,
        "unused_variables": 5,
        "indentation": 10,
        "magic_numbers": 5,
        "duplication": 15
    }
    for issue in issues:
        if "High code complexity" in issue:
            weights["complexity"] += 5
        if "Long functions" in issue:
            weights["function_length"] += 5
        if "Low comment density" in issue:
            weights["comment_density"] += 5
        if "Unused variables" in issue:
            weights["unused_variables"] += 5
        if "Inconsistent indentation" in issue:
            weights["indentation"] += 5
        if "Magic numbers detected" in issue:
            weights["magic_numbers"] += 5
        if "Code duplication detected" in issue:
            weights["duplication"] += 5
    total = sum(weights.values())
    return {k: (v / total) * 100 for k, v in weights.items()}

def analyze_code(filepath, language):
    try:
        with open(filepath, 'r') as f:
            code = f.read()
    except Exception as e:
        return None, [f"Error reading file: {e}"]

    issues = []
    syntax_funcs = {
        'python': analyze_python_syntax,
        'ruby': analyze_ruby_syntax,
        'php': analyze_php_syntax,
        'java': analyze_java_syntax
    }

    if language in syntax_funcs:
        issues.extend(syntax_funcs[language](code))
    else:
        return None, ["Unsupported language for syntax analysis."]

    complexity = analyze_complexity(code)
    if complexity > 10:
        issues.append(f"High code complexity: {complexity} conditionals found.")
    long_funcs = analyze_function_length(code)
    if long_funcs:
        issues.append(f"Long functions: {len(long_funcs)} exceeding 50 lines.")
    comment_density = analyze_comments(code)
    if comment_density < 0.1:
        issues.append(f"Low comment density: {comment_density*100:.2f}% comments.")
    unused_vars = analyze_unused_variables(code)
    if unused_vars:
        issues.append(f"Unused variables: {', '.join(unused_vars)}")
    indentation_issues = analyze_indentation(code)
    if indentation_issues:
        issues.append(f"Inconsistent indentation in {len(indentation_issues)} lines.")
    magic_numbers = analyze_magic_numbers(code)
    if magic_numbers:
        issues.append(f"Magic numbers detected: {', '.join(map(str, magic_numbers))}")
    duplication_count = analyze_code_duplication(code)
    if duplication_count > 0:
        issues.append(f"Code duplication detected: {duplication_count} duplicated lines.")

    dynamic_weights = calculate_weights(issues)
    scores = {
        "syntax": 1 if len(issues) == 0 else 0.5,
        "complexity": 0.5 if complexity > 10 else 1,
        "function_length": 0.5 if long_funcs else 1,
        "comment_density": 1 if comment_density >= 0.1 else 0.5,
        "unused_variables": 1 if not unused_vars else 0.5,
        "indentation": 1 if not indentation_issues else 0.5,
        "magic_numbers": 1 if not magic_numbers else 0.5,
        "duplication": 1 if duplication_count == 0 else 0.5
    }
    rating = sum(scores[k] * dynamic_weights[k] for k in scores)
    final_rating = round((rating / 100) * 5, 2)
    return final_rating, issues

def analyze_zip_input(zip_filepath):
    """
    Accepts a zip file path, extracts its contents to a temporary folder,
    analyzes each allowed code file, and returns a summary string.
    """
    temp_dir = os.path.join(os.path.dirname(zip_filepath), "extracted")
    os.makedirs(temp_dir, exist_ok=True)
    with zipfile.ZipFile(zip_filepath, 'r') as zip_ref:
        zip_ref.extractall(temp_dir)

    all_ratings = []
    all_issues = []
    total_rating = 0
    file_count = 0

    for root, _, files in os.walk(temp_dir):
        for filename in files:
            is_allowed, lang = allowed_file(filename)
            if is_allowed:
                file_path = os.path.join(root, filename)
                rating, issues = analyze_code(file_path, lang)
                all_ratings.append((filename, rating))
                all_issues.append((filename, issues))
                total_rating += rating
                file_count += 1

    overall_rating = round(total_rating / file_count, 2) if file_count else 0
    result_str = f"Overall Rating: {overall_rating}\n\n"
    for (fname, rating), (_, issues) in zip(all_ratings, all_issues):
        result_str += f"File: {fname}\nRating: {rating}\nIssues:\n"
        for issue in issues:
            result_str += f"  - {issue}\n"
        result_str += "\n"

    # Optionally, clean up extracted files:
    # shutil.rmtree(temp_dir)
    return result_str