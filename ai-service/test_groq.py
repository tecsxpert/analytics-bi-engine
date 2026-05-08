from services.groq_client import call_groq

prompt_template = open("prompts/describe.txt").read()
print("Prompt template loaded:")
print(prompt_template)
print("---")

item = "Q1 2026 revenue: $2.3M, up 12% YoY, APAC region"
prompt = prompt_template.replace("{item}", item)
print("Final prompt:")
print(prompt)
print("---")

result = call_groq(prompt)
print("Result:", result)