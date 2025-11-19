# Base image
IMAGE_NAME="llmsuite-experiment-image"

docker build -t $IMAGE_NAME .
docker build -f 8.Dockerfile -t $IMAGE_NAME-8 .
docker build -f 17.Dockerfile -t $IMAGE_NAME-17 .