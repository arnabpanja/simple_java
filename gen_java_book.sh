# generate the book 
jupyter nbconvert --to html --HTMLExporter.embed_images=True '/home/arnabp/projects/simple_java/src/main/java/com/arnabp/SimpleJava.ipynb' --template my_scroll_style --output 'index' --output-dir='/home/arnabp/projects/simple_java'
