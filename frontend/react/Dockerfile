FROM node:23-alpine
WORKDIR /app
COPY package*.json .
RUN npm i
COPY . .
RUN echo "VITE_API_BASE_URL=http://host.docker.internal:8088" > .env
EXPOSE 5173
CMD ["npm","run","dev"]