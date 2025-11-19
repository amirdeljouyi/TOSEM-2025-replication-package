docker stop llmsuite-1 llmsuite-2 llmsuite-3 llmsuite-4 llmsuite-5
docker stop codamosa-1 codamosa-2 codamosa-3 codamosa-4 codamosa-5
docker stop llminputonly-1 llminputonly-2 llminputonly-3 llminputonly-4 llminputonly-5
docker stop llmsuite-llama-1 llmsuite-llama-2 llmsuite-llama-3 llmsuite-llama-4 llmsuite-llama-5
docker stop llmsuite-file-1 llmsuite-file-2 llmsuite-file-3 llmsuite-file-4 llmsuite-file-5
docker stop evo-1 evo-2 evo-3 evo-4 evo-5
docker stop llmsuite-8-1 llmsuite-8-2 llmsuite-8-3 llmsuite-8-4 llmsuite-8-5
docker stop llminputonly-8-1 llminputonly-8-2 llminputonly-8-3 llminputonly-8-4 llminputonly-8-5
docker stop codamosa-8-1 codamosa-8-2 codamosa-8-3 codamosa-8-4 codamosa-8-5
docker stop llmsuite-llama-8-1 llmsuite-llama-8-2 llmsuite-llama-8-3 llmsuite-llama-8-4 llmsuite-llama-8-5
docker stop llmsuite-file-8-1 llmsuite-file-8-2 llmsuite-file-8-3 llmsuite-file-8-4 llmsuite-file-8-5
docker stop evo-8-1 evo-8-2 evo-8-3 evo-8-4 evo-8-5
docker stop llmsuite-17-1 llmsuite-17-2 llmsuite-17-3 llmsuite-17-4 llmsuite-17-5
docker stop llminputonly-17-1 llminputonly-17-2 llminputonly-17-3 llminputonly-17-4 llminputonly-17-5
docker stop codamosa-17-1 codamosa-17-2 codamosa-17-3 codamosa-17-4 codamosa-17-5
docker stop llmsuite-llama-17-1 llmsuite-llama-17-2 llmsuite-llama-17-3 llmsuite-llama-17-4 llmsuite-llama-17-5
docker stop llmsuite-file-17-1 llmsuite-file-17-2 llmsuite-file-17-3 llmsuite-file-17-4 llmsuite-file-17-5
docker stop evo-17-1 evo-17-2 evo-17-3 evo-17-4 evo-17-5
docker system prune -f &
sleep 5
docker image rm llmsuite-experiment-image
docker image rm llmsuite-experiment-image-8
docker image rm llmsuite-experiment-image-17