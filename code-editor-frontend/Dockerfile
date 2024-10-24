# Use the official Node.js image as the base image
FROM node:18-alpine as build

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install the dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

RUN npm install -g serve

# Build the application
RUN npm run build

#FROM serve:latest
#WORKDIR /app
#COPY --from=build /app/build ./build
# Expose the port the app will run on
EXPOSE 3000

CMD ["serve", "-s", "build"]
