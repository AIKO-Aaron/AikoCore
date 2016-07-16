cd "$(DIRNAME "$0")"
git add .
read MES
git commit -m "$MES"
git push origin master 
